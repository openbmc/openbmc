#!/bin/sh
echo "v4test starting:"
for t in $(find -maxdepth 1 -type f \! -name test\*_v6 -name test\*); do
  echo "$t";
  if $t; then
    echo "PASS: $t"; echo "";
  else
    echo "FAIL: $t"; echo "";
  fi
  sleep 1;
done
