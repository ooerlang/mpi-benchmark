#!/bin/bash

BASE_DIR=`pwd`

# Available Languages
# 1. elixir
# 2. erlang
# 3. ooerlang
# 4. java
# 5. scala
# 6. python

LANG_LIST="elixir erlang ooerlang java scala python ruby"
# LANG_LIST="elixir erlang ooerlang java scala python ruby" # Default

mkdir -p output

# Available Benchmark
# 1. pingping
# 2. pingpong
# 3. sendrecv

BENCH_LIST="pingping pingpong sendrecv"
# BENCH_LIST="pingping pingpong sendrecv" # Default

ITERATION_NUM="1"
# ITERATION_NUM="10" # Default

# pingping & pingpong configuration
PG_MSG_SIZE_LIST="5120 10240 51200 102400"
PG_LOOP_NUM_LIST="5000 10000 50000 100000 500000 1000000 5000000"

# Default
# PG_MSG_SIZE_LIST="5120 10240 51200 102400"
# PG_LOOP_NUM_LIST="5000 10000 50000 100000 500000 1000000 5000000"

# sendrecv configuration
SR_MSG_LIST="5120 10240 51200"
SR_LOOP_NUM_LIST="5000 10000 50000 100000"
SR_PROC_NUM_LIST="1000 10000 50000"

# Default
# SR_MSG_LIST="5120 10240 51200"
# SR_LOOP_NUM_LIST="5000 10000 50000 100000"
# SR_PROC_NUM_LIST="1000 10000 50000"

# clean compiled files
clean() {
    case $1 in
        elixir)   rm -f *.beam;;
        erlang)   rm -f *.beam;;
        ooerlang) rm -f *.beam proc.erl pingping.erl pingpong.erl sendrecv.erl;;
        java)     rm -f *.class;;
        scala)    rm -f *.class;;
        python)   rm -f *.pyc;;
        ruby)     ;;
    esac
}

build() {
    case $1 in
        elixir) elixirc *.ex --erl -smp;;
        erlang) erlc -smp *.erl;;
        ooerlang) erl -noshell \
            -pa ${OOERL_DIR}/ebin \
            -eval "ooec:compile(filelib:wildcard(\"*.cerl\"))" \
            -s init stop
            rm -f *.beam
            erlc -smp *.erl;;
        java)   javac *.java;;
        scala)  scalac *.scala;;
        python) python compile.py;;
        ruby)   ;;
    esac
}

run_pingping() {
    case $1 in
        elixir)   elixir -e "Pingping.run($2, $3)" --erl '-smp enable';;
        erlang)   erl -smp enable -noshell \
            -eval "pingping:run($2, $3)." \
            -s init stop;;
        hipe)     erl -smp enable -noshell \
            -eval "pingping:run($2, $3)." \
            -s init stop;;
        ooerlang) erl -smp enable -noshell \
            -pa ${OOERL_DIR}/ebin \
            -eval "pingping:run($2, $3)." \
            -s init stop;;
        java)     java -Xms1G -Xmx2G PingPingMain $2 $3;;
        scala)    scala -J-Xms1G -J-Xmx2G pingping $2 $3;;
        python)   python pingping.pyc $2 $3;;
        ruby)     ruby pingping.rb $2 $3;;
    esac
}

run_pingpong() {
    case $1 in
        elixir)   elixir -e "Pingpong.run($2, $3)" --erl '-smp enable';;
        erlang)   erl -smp enable -noshell \
            -eval "pingpong:run($2, $3)." \
            -s init stop;;
        ooerlang) erl -smp enable -noshell \
            -pa ${OOERL_DIR}/ebin \
            -eval "pingpong:run($2, $3)." \
            -s init stop;;
        java)     java -Xms1G -Xmx2G PingPongMain $2 $3;;
        scala)    scala -J-Xms1G -J-Xmx2G pingpong $2 $3;;
        python)   python pingpong.pyc $2 $3;;
        ruby)     ruby pingpong.rb $2 $3;;
    esac
}

run_sendrecv() {
    case $1 in
        elixir)   elixir -e "SendRecv.run($2, $3, $4)" --erl '-smp enable';;
        erlang)   erl -smp enable -noshell \
            +P 1000000 \
            -eval "sendrecv:run($2, $3, $4)." \
            -s init stop;;
        hipe)     erl -smp enable -noshell \
            +P 1000000 \
            -eval "sendrecv:run($2, $3, $4)." \
            -s init stop;;
        ooerlang) erl -smp enable -noshell \
            -pa ${OOERL_DIR}/ebin \
            +P 1000000 \
            -eval "sendrecv:run($2, $3, $4)." \
            -s init stop;;
        java)     java -Xms1G -Xmx2G SendRecvMain $2 $3 $4;;
        scala)    scala -J-Xms1G -J-Xmx2G sendrecv $2 $3 $4;;
        python)   ;;
        ruby)     ruby $2 $3 $4;;
    esac
}

if [[ "${BENCH_LIST}" == *pingping* ]]; then
    for i in `seq 1 $ITERATION_NUM`; do
    for lang in $LANG_LIST; do
        l=`cut -d'+' -f1 <<< $lang`
        cd $BASE_DIR/pingping/$l
        clean $lang
        build $lang
        for msgnum in $PG_LOOP_NUM_LIST; do
            for msgsize in $PG_MSG_SIZE_LIST; do
                echo "run $i pingpong $lang ${msgnum}x${msgsize}"
                run_pingping $lang $msgsize $msgnum >> ${BASE_DIR}/output/${lang}_pingping.txt
            done
        done
    done
    done
fi

if [[ "${BENCH_LIST}" == *pingpong* ]]; then
    for i in `seq 1 $ITERATION_NUM`; do
        for lang in $LANG_LIST; do
            l=`cut -d'+' -f1 <<< $lang`
            cd $BASE_DIR/pingpong/$l
            clean $lang
            build $lang
            for msgnum in $PG_LOOP_NUM_LIST; do
                for msgsize in $PG_MSG_SIZE_LIST; do
                    echo "run $i pingpong $lang ${msgnum}x${msgsize}"
                    run_pingpong $lang $msgsize $msgnum >> ${BASE_DIR}/output/${lang}_pingpong.txt
                done
            done
        done
    done
fi

if [[ "${BENCH_LIST}" == *sendrecv* ]]; then
    for i in `seq 1 $ITERATION_NUM`; do
        for lang in $LANG_LIST; do
            l=`cut -d'+' -f1 <<< $lang`
            cd $BASE_DIR/sendrecv/$l
            clean $lang
            build $lang
            for proc in $SR_PROC_NUM_LIST; do
                for msgnum in $SR_LOOP_NUM_LIST; do
                    for msgsize in $SR_MSG_LIST; do
                        echo "run $i sendrecv $lang ${msgnum}x${msgsize}x${proc}"
                        run_sendrecv $lang $msgsize $msgnum $proc >> ${BASE_DIR}/output/${lang}_sendrecv.txt
                    done
                done
            done
        done
    done
fi
