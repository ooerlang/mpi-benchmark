-module(pingping).
-export([run/3]).

-include("conf.hrl").

run(PairNumber, LoopNumber, MessageSize) ->
    Message = generate_message(MessageSize),

    SpawnStart = time_microseg(),
    PidPairs = spawn_pairs(PairNumber, LoopNumber, Message),
    SpawnEnd = time_microseg(),

    TimeStart = time_microseg(),
    start_pairs(PidPairs),
    wait_finish(PidPairs),
    TimeEnd = time_microseg(),

    SpawnTime = SpawnEnd - SpawnStart,
    TotalTime = TimeEnd - TimeStart,

    print_result(PairNumber, LoopNumber, MessageSize, SpawnTime, TotalTime),
    {SpawnTime, TotalTime}.

spawn_pairs(PN, LN, Msg) ->
    spawn_pairs(PN, LN, Msg, self(), []).

spawn_pairs(0, _, _, _, Pids) ->
    Pids;
spawn_pairs(PN, LN, Msg, Self, Pids) ->
    P1 = spawn(fun() -> pingping(Msg, Self, LN) end),
    P2 = spawn(fun() -> pingping(Msg, Self, LN) end),
    spawn_pairs(PN - 1, LN, Msg, Self, [{P1, P2}|Pids]).

start_pairs([]) -> ok;
start_pairs([{P1, P2}|Pids]) ->
    P1 ! {init, self(), P2},
    P2 ! {init, self(), P1},
    start_pairs(Pids).

wait_finish([]) -> ok;
wait_finish([{P1, P2}|Pids]) ->
    finalize(P1),
    finalize(P2),
    wait_finish(Pids).

pingping(_,Parent, 0) ->
    Parent ! {finish, self()};

pingping(Data, Parent, R) ->
    receive
        {init, Parent, Peer} ->
            Peer ! {self(), Data},
            pingping(Data, Parent, R-1);
        {Peer, Data} ->
            Peer ! {self(), Data},
            pingping(Data, Parent, R-1)
    end.

finalize(Pid) ->
    receive
        {finish, Pid} ->
            ok
    end.

print_result(PN, LN, MS, SpawnTime, TotalTime) ->
    io:format("~w\t~w\t~w\t~w\t~w\n", [PN, LN, MS, SpawnTime, TotalTime]).

generate_message(Size) ->
    generate_message(Size, []).

generate_message(0, Bytes) ->
    list_to_binary(Bytes);
generate_message(Size, Bytes) ->
    generate_message(Size - 1, [1 | Bytes]).

time_microseg() ->
    {MS, S, US} = now(),
    (MS * 1.0e+12) + (S * 1.0e+6) + US.
