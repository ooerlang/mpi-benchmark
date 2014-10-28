defmodule Pingping do
  @moduledoc """
  MPI Benchmark [Pingping] in elixir by Filipe Varjão <frgv@cin.ufpe.br>
  """

  def run(pairNumber, rep, messageSize) do

    data = generate_data(messageSize)
    spawnStart = time_microseg()
    pidPairs = spawn_pairs(pairNumber, rep, data)
    spawnEnd = time_microseg()

    timeStart = time_microseg()
    start_pairs(pidPairs)
    wait_finish(pidPairs)
    timeEnd = time_microseg()

    totalTime = timeEnd - timeStart
    spawnTime = spawnEnd - spawnStart

    # PRINT RESULT
    IO.puts "bytes #{:erlang.size(data)} | repetitions #{rep} | exec_time[µsec] #{totalTime} | MBytes/sec #{spawnTime} | spawn_time #{bandwidth_calc(data, totalTime)}"

  end

  def spawn_pairs(pn, rep, data), do: spawn_pairs(pn, rep, data, self(), [])

  def spawn_pairs(0, _, _, _, pids), do: pids

  def spawn_pairs(pn, rep, data, parent, pids) do
    p1 = spawn(fn -> pingping(data, parent, rep) end)
    p2 = spawn(fn -> pingping(data, parent, rep) end)
    spawn_pairs(pn - 1, rep, data, parent, [{p1, p2}|pids])
  end

  def start_pairs([]), do: :ok

  def start_pairs([{p1, p2}|pids]) do
    send(p1, {:init, self, p2})
    send(p2, {:init, self, p1})
    start_pairs(pids)
  end

  def wait_finish([]), do: :ok

  def wait_finish([{p1, p2}|pids]) do
    finalize(p1)
    finalize(p2)
    wait_finish(pids)
  end

  def pingping(_, pid, 0), do: send(pid ,{:finish, self})

  def pingping(data, pid, r) do
    receive do
      {:init, ^pid, peer} ->
        send(peer, {self, data})
        pingping(data, pid, r - 1)
      {peer, data} ->
        send(peer, {self, data})
        pingping(data, pid, r - 1)
    end
  end
 
  def finalize(p1) do
    receive do
      {:finish, ^p1} ->
        :ok
    end
  end

  def bandwidth_calc(data, time) do
    megabytes = (:erlang.size(data) / :math.pow(2, 20))
    seconds = time * 1.0e-6
    megabytes / seconds
  end

  def generate_data(size), do: generate_data(size, [])

  def generate_data(0, bytes), do: IO.iodata_to_binary(bytes)

  def generate_data(size, bytes), do: generate_data(size - 1, [1 | bytes])

  def time_microseg() do
    {ms, s, us} = :erlang.now()
    (ms * 1.0e+12) + (s * 1.0e+6) + us
  end
end
