#!/bin/sh

# Copyright (C) 2012 Wind River Systems, Inc.
#
# This program is free software; you can redistribute it and/or modify
# it under the terms of the GNU General Public License version 2 as
# published by the Free Software Foundation.
#
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
# See the GNU General Public License for more details.
#
# You should have received a copy of the GNU General Public License
# along with this program; if not, write to the Free Software
# Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA


WORK_DIR="/opt/lsb-test"

if [ `id -u` -ne 0 ]
then
	cat << EOF
	In order to install and run LSB testsuite, you need administrator privileges.
	You are currently running this script as an unprivileged user.

EOF
	exit 1
fi

ARCH=`uname -m`
if [ ${ARCH} != "i686" ] && [ ${ARCH} != "x86_64" ] && [ ${ARCH} != "ppc" ] && [ ${ARCH} != "ppc64" ]
then
	echo "Error: Unsupported architecture"
	exit 1
fi

which rpm
if [ $? -ne 0 ]
then
	echo "No rpm command found"
	exit 1
fi

RET=0

cd ${WORK_DIR} || exit 1
# Step 1: Download the LSB Packages
echo ""
echo "Download LSB packages..."
echo ""

if [ ! -e ./packages_list ]
then
	echo "Error: Could not find packages list" >&2
	exit 1
fi

. ./packages_list

PACKAGES_DIR="/var/opt/lsb/test/manager/packages/ftp.linuxfoundation.org/pub/lsb"

BASE_PACKAGES_DIR="${PACKAGES_DIR}/base/${LSB_RELEASE}/binary"
RUNTIME_BASE_PACKAGES_DIR="${PACKAGES_DIR}/test_suites/released-all/binary/runtime"
RUNTIME_PACKAGES_DIR="${PACKAGES_DIR}/test_suites/${LSB_RELEASE}/binary/runtime"
APP_PACKAGES_DIR="${PACKAGES_DIR}/app-battery/${LSB_RELEASE}/${LSB_ARCH}"
APP_TESTFILES_DIR="${PACKAGES_DIR}/app-battery/tests"
SNAPSHOTS_TESTFILES_DIR="${PACKAGES_DIR}/snapshots/appbat/tests"

if [ ! -d ${PACKAGES_DIR} ]
then
	mkdir -p ${PACKAGES_DIR}
fi

if [ ! -d ${BASE_PACKAGES_DIR} ]
then
	mkdir -p ${BASE_PACKAGES_DIR}
fi

if [ ! -d ${RUNTIME_BASE_PACKAGES_DIR} ]
then
	mkdir -p ${RUNTIME_BASE_PACKAGES_DIR}
fi

if [ ! -d ${RUNTIME_PACKAGES_DIR} ]
then
	mkdir -p ${RUNTIME_PACKAGES_DIR}
fi

if [ ! -d ${APP_PACKAGES_DIR} ]
then
	mkdir -p ${APP_PACKAGES_DIR}
fi

if [ ! -d ${APP_TESTFILES_DIR} ]
then
	mkdir -p ${APP_TESTFILES_DIR}
fi

# Official download server list. You can replace them with your own server.
SERVER_IPADDR="140.211.169.23"
SERVER_NAME="ftp.linuxfoundation.org"

if ! `grep -F -q "${SERVER_NAME}" /etc/hosts`; then
	echo "${SERVER_IPADDR}	${SERVER_NAME}	${SERVER_NAME}" >> /etc/hosts
fi

#ping -c 5 ${SERVER_NAME}
#if [ $? -ne 0 ]
#then
#	echo "The server: ${SERVER_NAME} is unreachable"
#	exit 1
#fi

SERVER1="\
	http://${SERVER_NAME}/pub/lsb/base/${LSB_RELEASE}/binary"
SERVER2="\
	http://${SERVER_NAME}/pub/lsb/test_suites/released-all/binary/runtime"
SERVER3="\
	http://${SERVER_NAME}/pub/lsb/test_suites/${LSB_RELEASE}/binary/runtime"
SERVER4="\
	http://${SERVER_NAME}/pub/lsb/app-battery/${LSB_RELEASE}/${LSB_ARCH}"
SERVER5="\
	http://${SERVER_NAME}/pub/lsb/app-battery/tests"

# We using "curl" as a download tool, "wget" is an alternative.
CURL=`which curl`
WGET=`which wget`
if [ ! -z ${CURL} ]
then
	DOWNLOAD_CMD="${CURL} -R -L -f --retry 3 --retry-delay 4 --connect-timeout 180 --compressed -C - -o"
elif [ ! -z ${WGET} ]
then
	DOWNLOAD_CMD="${WGET} -c -t 5 -O"
else
	echo "Can not find a download tool, please install curl or wget."
	exit 1
fi

cd ${BASE_PACKAGES_DIR}
for pkg in ${BASE_PACKAGES_LIST}; do
	if [ ! -f ${pkg} ]
	then
		#${DOWNLOAD_CMD} ${pkg}".#part" ${SERVER1}/${pkg} > /dev/null 2>&1
		${DOWNLOAD_CMD} ${pkg}".#part" ${SERVER1}/${pkg}
		if [ $? -eq 0 ]
		then
			mv -f ${pkg}".#part" ${pkg}
			echo "Download ${pkg} successfully."
		else
			echo "Download ${pkg} failed."
			RET=1
		fi
	fi
done

cd ${RUNTIME_BASE_PACKAGES_DIR}
for pkg in ${RUNTIME_BASE_PACKAGES_LIST}; do
	if [ ! -f ${pkg} ]
	then
		#${DOWNLOAD_CMD} ${pkg}".#part" ${SERVER2}/${pkg} > /dev/null 2>&1
		${DOWNLOAD_CMD} ${pkg}".#part" ${SERVER2}/${pkg}
		if [ $? -eq 0 ]
		then
			mv -f ${pkg}".#part" ${pkg}
			echo "Download ${pkg} successfully."
		else
			echo "Download ${pkg} failed."
			RET=1
		fi
	fi
done

cd ${RUNTIME_PACKAGES_DIR}
for pkg in ${RUNTIME_PACKAGES_LIST}; do
	if [ ! -f ${pkg} ]
	then
		#${DOWNLOAD_CMD} ${pkg}".#part" ${SERVER3}/${pkg} > /dev/null 2>&1
		${DOWNLOAD_CMD} ${pkg}".#part" ${SERVER3}/${pkg}
		if [ $? -eq 0 ]
		then
			mv -f ${pkg}".#part" ${pkg}
			echo "Download ${pkg} successfully."
		else
			echo "Download ${pkg} failed."
			RET=1
		fi
	fi
done

cd ${APP_PACKAGES_DIR}
for pkg in ${APP_PACKAGES_LIST}; do
	if [ ! -f ${pkg} ]
	then
		#${DOWNLOAD_CMD} ${pkg}".#part" ${SERVER4}/${pkg} > /dev/null 2>&1
		${DOWNLOAD_CMD} ${pkg}".#part" ${SERVER4}/${pkg}
		if [ $? -eq 0 ]
		then
			mv -f ${pkg}".#part" ${pkg}
			echo "Download ${pkg} successfully."
		else
			echo "Download ${pkg} failed."
			RET=1
		fi
	fi
done

cd ${APP_TESTFILES_DIR}
for pkg in ${APP_TESTFILES_LIST}; do
	if [ ! -f ${pkg} ]
	then
		#${DOWNLOAD_CMD} ${pkg}".#part" ${SERVER5}/${pkg} > /dev/null 2>&1
		${DOWNLOAD_CMD} ${pkg}".#part" ${SERVER5}/${pkg}
		if [ $? -eq 0 ]
		then
			mv -f ${pkg}".#part" ${pkg}
			echo "Download ${pkg} successfully."
		else
			echo "Download ${pkg} failed."
			RET=1
		fi
	fi
done

if [ ${RET} -ne 0 ]
then
	echo "Download some packages failed. Please download them again."
	exit 1
fi

# Step 2: Install the LSB Packages
echo ""
echo "Install LSB packages..."
echo ""

# Kill lighttpd
ps aux | grep "lighttpd" | grep -v -q "grep"
if [ $? -eq 0 ]
then
	killall lighttpd >/dev/null 2>&1
fi

# Start avahi-daemon
ps aux | grep "avahi-daemon" | grep -v -q "grep"
if [ $? -ne 0 ]
then
	/etc/init.d/avahi-daemon start >/dev/null 2>&1
fi

LSB_START_CMD="/opt/lsb/test/manager/bin/dist-checker-start.pl"
LSB_STOP_CMD="/opt/lsb/test/manager/bin/dist-checker-stop.pl"

PLATFORM_FILE="/etc/rpm/platform"

RPM_INSTALL_CMD="rpm --quiet --nodeps --replacepkgs --nosignature -i"
RPM_INSTALL_CMD_NOSCRIPTS="rpm --quiet --nodeps --replacepkgs --noscripts --nosignature -i"

# If the lsb has been started, stop it first.
if [ -x ${LSB_STOP_CMD} ]
then
	${LSB_STOP_CMD}
fi

if [ ! -d /etc/rpm ]
then
	mkdir -p /etc/rpm
fi

if [ ! -f ${PLATFORM_FILE} ]
then
	touch ${PLATFORM_FILE}
fi

if ! `grep -F -q "noarch-suse" ${PLATFORM_FILE}`; then
	if [ ${ARCH} = i686 ];then
		echo "i486-suse" >> ${PLATFORM_FILE}
		echo "i486-noarch" >> ${PLATFORM_FILE}
		echo "i486-pc" >> ${PLATFORM_FILE}
		echo "noarch-suse" >> ${PLATFORM_FILE}
	elif [ ${ARCH} = x86_64 ]; then
		echo "i486-suse" >> ${PLATFORM_FILE}
		echo "i486-noarch" >> ${PLATFORM_FILE}
		echo "i486-pc" >> ${PLATFORM_FILE}
		echo "i486-.*-linux.*" >> ${PLATFORM_FILE}
		echo "noarch-suse" >> ${PLATFORM_FILE}
		echo "${ARCH}-suse" >> ${PLATFORM_FILE}
		echo "${ARCH}-noarch" >> ${PLATFORM_FILE}
		echo "${ARCH}-pc" >> ${PLATFORM_FILE}
	else
		echo "${ARCH}-suse" >> ${PLATFORM_FILE}
		echo "${ARCH}-noarch" >> ${PLATFORM_FILE}
		echo "${ARCH}-pc" >> ${PLATFORM_FILE}
		echo "noarch-suse" >> ${PLATFORM_FILE}
	fi
fi

if [ -d ${BASE_PACKAGES_DIR} ]
then
	cd ${BASE_PACKAGES_DIR}
	for pkg in ${BASE_PACKAGES_LIST}
	do
		rpm --quiet -q ${pkg%\.*}
		if [ $? -ne 0 ]; then
			$RPM_INSTALL_CMD ${pkg}
		fi
	done
fi

if [ -d ${RUNTIME_BASE_PACKAGES_DIR} ]
then
	cd ${RUNTIME_BASE_PACKAGES_DIR}
	for pkg in ${RUNTIME_BASE_PACKAGES_LIST}
	do
		rpm --quiet -q ${pkg%\.*}
		if [ $? -ne 0 ]; then
			$RPM_INSTALL_CMD ${pkg}
		fi
	done
fi

if [ -d ${RUNTIME_PACKAGES_DIR} ]
then
	cd ${RUNTIME_PACKAGES_DIR}
	for pkg in ${RUNTIME_PACKAGES_LIST}
	do
		rpm --quiet -q ${pkg%\.*}
		if [ $? -ne 0 ]; then
			$RPM_INSTALL_CMD ${pkg}
		fi
	done
fi

if [ -d ${APP_PACKAGES_DIR} ]
then
	cd ${APP_PACKAGES_DIR}
	for pkg in ${APP_PACKAGES_LIST}
	do
		echo "${pkg}" | grep -q "apache\|xpdf"
		if [ $? -eq 0 ]
		then
			rpm --quiet -q ${pkg%\.*}
			if [ $? -ne 0 ]; then
				$RPM_INSTALL_CMD_NOSCRIPTS ${pkg}
			fi
		else
			rpm --quiet -q ${pkg%\.*}
			if [ $? -ne 0 ]; then
				$RPM_INSTALL_CMD ${pkg}
			fi
		fi
	done
fi

if [ ! -d ${SNAPSHOTS_TESTFILES_DIR} ]
then
	mkdir -p ${SNAPSHOTS_TESTFILES_DIR}
fi

if [ -d ${APP_TESTFILES_DIR} ]
then
	cd ${APP_TESTFILES_DIR}
	for pkg in ${APP_TESTFILES_LIST}
	do
		cp -f ${pkg} ${SNAPSHOTS_TESTFILES_DIR}
	done
fi

cd ${WORK_DIR}

# Step 3: Set environment
echo ""
echo "Set environment..."
echo ""

check ()
{
	if [ $? -eq 0 ]
	then
		echo "PASS"
	else 
		echo "FAIL"
		exit 1
	fi
}

echo ""
echo "---------------------------------"
echo "Create the Dirnames on target"

if [ ! -d /etc/rpm/sysinfo ]
then
	mkdir -p /etc/rpm/sysinfo
fi

cat > /etc/rpm/sysinfo/Dirnames << EOF
/etc/opt/lsb
/home/tet/LSB.tools
/opt/lsb-tet3-lite/lib/ksh
/opt/lsb-tet3-lite/lib/perl
/opt/lsb-tet3-lite/lib/posix_sh
/opt/lsb-tet3-lite/lib/tet3
/opt/lsb-tet3-lite/lib/xpg3sh
/opt/lsb/appbat/lib/python2.4/site-packages/qm
/opt/lsb/appbat/lib/python2.4/site-packages/qm/external
/opt/lsb/appbat/lib/python2.4/site-packages/qm/external/DocumentTemplate
/opt/lsb/appbat/lib/python2.4/site-packages/qm/test
/opt/lsb/appbat/lib/python2.4/site-packages/qm/test/classes
/opt/lsb/appbat/lib/python2.4/site-packages/qm/test/web
/opt/lsb/test/doc
/opt/lsb/test/lib
/opt/lsb/test/qm/diagnostics
/opt/lsb/test/qm/doc
/opt/lsb/test/qm/doc/test/html
/opt/lsb/test/qm/doc/test/print
/opt/lsb/test/qm/dtml
/opt/lsb/test/qm/dtml/test
/opt/lsb/test/qm/messages/test
/opt/lsb/test/qm/tutorial/test/tdb
/opt/lsb/test/qm/tutorial/test/tdb/QMTest
/opt/lsb/test/qm/web
/opt/lsb/test/qm/web/images
/opt/lsb/test/qm/web/stylesheets
/opt/lsb/test/qm/xml
/opt/lsb/test/share
/usr/share/doc/lsb-runtime-test
/var/opt/lsb
/opt/lsb/test/desktop
/opt/lsb/test/desktop/fontconfig
/opt/lsb/test/desktop/freetype
/opt/lsb/test/desktop/gtkvts
/opt/lsb/test/desktop/libpng
/opt/lsb/test/desktop/qt3
/opt/lsb/test/desktop/xft
/opt/lsb/test/desktop/xml
/opt/lsb/test/desktop/xrender


EOF

if [ -f /etc/rpm/sysinfo/Dirnames ]
then
	echo "Success to creat Dirnames file"
else 
	echo "Fail to creat Dirnames file"
fi

[ -x /sbin/ldconfig ] && {
echo ""
echo "---------------------------------"
echo "Update cache"
/sbin/ldconfig
check;
}

# Check loop device
if [ ! -b /dev/loop0 ]
then
	insmod /lib/modules/`uname -r`/kernel/drivers/block/loop.ko
	if [ $? != 0 ];then
		echo "Insmod loop.ko failed."
	fi
fi

# Resolve localhost
LOCALHOST=`hostname`
if ! `grep -F -q "$LOCALHOST" /etc/hosts`; then
	echo "127.0.0.1	$LOCALHOST" >> /etc/hosts
fi

# Workaround to add part of locales for LSB test
localedef -i de_DE -f ISO-8859-1 de_DE
localedef -i de_DE -f ISO-8859-15 de_DE.ISO-8859-15
localedef -i de_DE -f UTF-8 de_DE.UTF-8
localedef -i de_DE@euro -f ISO-8859-15 de_DE@euro
localedef -i en_HK -f ISO-8859-1 en_HK
localedef -i en_PH -f ISO-8859-1 en_PH
localedef -i en_US -f ISO-8859-15 en_US.ISO-8859-15
localedef -i en_US -f ISO-8859-1 en_US.ISO-8859-1
localedef -i en_US -f ISO-8859-1 en_US
localedef -i en_US -f UTF-8 en_US.UTF-8
localedef -i en_US -f ISO-8859-1 en_US.ISO8859-1
localedef -i es_MX -f ISO-8859-1 es_MX
localedef -i fr_FR -f ISO-8859-1 fr_FR
localedef -i it_IT -f ISO-8859-1 it_IT
localedef -i ja_JP -f EUC-JP ja_JP.eucjp
localedef -i se_NO -f UTF-8 se_NO.UTF-8
localedef -i ta_IN -f UTF-8 ta_IN
localedef -i es_ES -f ISO-8859-1 es_ES
localedef -i fr_FR@euro -f ISO-8859-1 fr_FR@euro
localedef -i is_IS -f UTF-8 is_IS.UTF-8
localedef -i zh_TW -f BIG5 zh_TW.BIG5
localedef -i en_US -f ISO-8859-15 en_US.ISO-8859-15

echo ""
echo "Installation done!"
echo ""

# Step 4: Start LSB test
if [ -x ${LSB_START_CMD} ]
then
	${LSB_START_CMD}
fi

echo "---------------------------------"
echo "Run all the certification version of LSB Tests"
echo "---------------------------------"

LSB_DIST_CHECKER="/opt/lsb/test/manager/utils/dist-checker.pl"
SESSION="${WORK_DIR}/session"
if [ ! -e ${SESSION} ]
then
	echo "Error: Could not find session file."
	echo "You must run LSB test from webbrower."
	exit 1
fi

if [ -x ${LSB_DIST_CHECKER} ]
then
	${LSB_DIST_CHECKER} -v2 -f ${SESSION}
	check
fi

echo ""
echo "LSB test complete. Please check the log file in /var/opt/lsb/test/manager/results/"
echo ""

