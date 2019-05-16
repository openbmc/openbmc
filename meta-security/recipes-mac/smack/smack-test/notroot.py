#!/usr/bin/env python
#
# Script used for running executables with custom labels, as well as custom uid/gid
# Process label is changed by writing to /proc/self/attr/curent
#
# Script expects user id and group id to exist, and be the same.
#
# From adduser manual: 
# """By  default,  each  user  in Debian GNU/Linux is given a corresponding group 
# with the same name. """
#
# Usage: root@desk:~# python notroot.py <uid> <label> <full_path_to_executable> [arguments ..]
# eg: python notroot.py 1000 User::Label /bin/ping -c 3 192.168.1.1
#
# Author: Alexandru Cornea <alexandru.cornea@intel.com>
import os
import sys

try:
	uid = int(sys.argv[1])
	sys.argv.pop(1)
	label = sys.argv[1]
	sys.argv.pop(1)
	open("/proc/self/attr/current", "w").write(label)
	path=sys.argv[1]
	sys.argv.pop(0)
	os.setgid(uid)
	os.setuid(uid)	
	os.execv(path,sys.argv)

except Exception,e:
	print e.message
	sys.exit(1)
