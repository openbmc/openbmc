#!/bin/sh
echo "v6test starting:"
for t in $(find -maxdepth 1 -name test\*_v6); do
  echo "$t";
  if $t; then
    echo "PASS: $t"; echo "";
  else
    echo "FAIL: $t"; echo "";
  fi
  sleep 1;
done
