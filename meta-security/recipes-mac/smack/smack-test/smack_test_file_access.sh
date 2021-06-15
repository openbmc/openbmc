#!/bin/sh

SMACK_PATH=`grep smack /proc/mounts | awk '{print $2}' `
RC=0
TMP="/tmp"
test_file=$TMP/smack_test_access_file
CAT=`which cat`
ECHO=`which echo`
uid=1000
initial_label=`cat /proc/self/attr/current`
python $TMP/notroot.py $uid "TheOther" $ECHO 'TEST' > $test_file
chsmack -a "TheOther" $test_file

#        12345678901234567890123456789012345678901234567890123456
delrule="TheOne                  TheOther                -----"
rule_ro="TheOne                  TheOther                r----"

# Remove pre-existent rules for "TheOne TheOther <access>"
echo -n "$delrule" > $SMACK_PATH/load
python $TMP/notroot.py $uid "TheOne" $CAT $test_file 2>&1 1>/dev/null | grep -q "Permission denied" || RC=$?
if [ $RC -ne 0 ]; then
	echo "Process with different label than the test file and no read access on it can read it"
	exit $RC
fi

# adding read access
echo -n "$rule_ro" > $SMACK_PATH/load
python $TMP/notroot.py $uid "TheOne" $CAT $test_file | grep -q "TEST" || RC=$?
if [ $RC -ne 0 ]; then
	echo "Process with different label than the test file but with read access on it cannot read it"
	exit $RC
fi

# Remove pre-existent rules for "TheOne TheOther <access>"
echo -n "$delrule" > $SMACK_PATH/load
# changing label of test file to *
# according to SMACK documentation, read access on a * object is always permitted
chsmack -a '*' $test_file
python $TMP/notroot.py $uid "TheOne" $CAT $test_file | grep -q "TEST" || RC=$?
if [ $RC -ne 0 ]; then
	echo  "Process cannot read file with * label"
	exit $RC
fi

# changing subject label to *
# according to SMACK documentation, every access requested by a star labeled subject is rejected
TOUCH=`which touch`
python $TMP/notroot.py $uid '*' $TOUCH $TMP/test_file_2
ls -la $TMP/test_file_2 2>&1 | grep -q 'No such file or directory' || RC=$?
if [ $RC -ne 0 ];then
	echo "Process with label '*' should not have any access"
	exit $RC
fi
exit 0
