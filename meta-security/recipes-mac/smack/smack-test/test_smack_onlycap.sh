#!/bin/sh
RC=0
SMACK_PATH=`grep smack /proc/mounts | awk '{print $2}'`
test_label="test_label"
onlycap_initial=`cat $SMACK_PATH/onlycap`		
smack_initial=`cat /proc/self/attr/current`

# need to set out label to be the same as onlycap, otherwise we lose our smack privileges
# even if we are root
echo "$test_label" > /proc/self/attr/current

echo "$test_label" > $SMACK_PATH/onlycap || RC=$?
if [ $RC -ne 0 ]; then
	echo "Onlycap label could not be set"
	return $RC
fi

if [ `cat $SMACK_PATH/onlycap` != "$test_label" ]; then
	echo "Onlycap label was not set correctly."
	return 1
fi

# resetting original onlycap label
echo "$onlycap_initial" > $SMACK_PATH/onlycap 2>/dev/null

# resetting our initial's process label
echo "$smack_initial" > /proc/self/attr/current
