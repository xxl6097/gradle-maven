#!/bin/bash
function todir() {
  pwd
}

function pull() {
  todir
  echo "git pull."
  git pull
}

function forcepullmaster() {
  todir
  echo "git fetch --all && git reset --hard origin/master && git pull"
  git fetch --all && git reset --hard origin/master && git pull
}

function forcepullmain() {
  todir
  echo "git fetch --all && git reset --hard origin/main && git pull"
  git fetch --all && git reset --hard origin/main && git pull
}


# shellcheck disable=SC2120
function gitpush() {
  commit=""
  if [ ! -n "$1" ]; then
    commit="$(date '+%Y-%m-%d %H:%M:%S') by ${USER}"
  else
    commit="$1 by ${USER}"
  fi

  echo $commit
  git add .
  git commit -m "$commit"
  #  git push -u origin main
  git push
}

function menu() {
    echo "1. 强制更新[origin/master]"
    echo "2. 强制更新[origin/main]"
    echo "3. 普通更新"
    echo "4. 提交项目"
    echo "请输入编号:"
    read index

    case "$index" in
    [1]) (forcepullmaster);;
    [2]) (forcepullmain);;
    [3]) (pull);;
    [4]) (gitpush);;
    *) echo "exit" ;;
  esac
}

function bootstrap() {
    case $1 in
    pull) (pull) ;;
    menu) (menu) ;;
      -f) (forcepull) ;;
       *) ( gitpush $1)  ;;
    esac
}


bootstrap $1
