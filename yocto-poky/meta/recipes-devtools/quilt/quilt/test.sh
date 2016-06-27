for i in `ls test/*.test |awk -F. '{print $1}' |awk -F/ '{print $2}'`; do make check-$i; if [ $? -eq 0 ]; then echo PASS: $i.test; else echo FAIL: $i.test; fi; done
