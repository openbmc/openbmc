#! /bin/sh

#
# Generate the common perl configuration
# Needs to be run on a host that matches the bitsize of the target platform
#

echo sh Configure -des \
        -Doptimize=-O2 \
        -Dmyhostname=localhost \
        -Dperladmin=root@localhost \
        -Dcc=gcc \
        -Dcf_by='Open Embedded' \
        -Dinstallprefix=@DESTDIR@ \
        -Dprefix=/usr \
        -Dvendorprefix=/usr \
        -Dsiteprefix=/usr \
        -Dotherlibdirs=/usr/lib/perl5/5.22.1 \
        -Duseshrplib \
        -Dusethreads \
        -Duseithreads \
        -Duselargefiles \
        -Ud_dosuid \
        -Dd_semctl_semun \
        -Ui_db \
        -Ui_ndbm \
        -Ui_gdbm \
        -Di_shadow \
        -Di_syslog \
        -Dman3ext=3pm \
        -Duseperlio \
        -Dinstallusrbinperl \
        -Ubincompat5005 \
        -Uversiononly \
        -Dpager='/usr/bin/less -isr'

cp -f config.sh config.sh.COMMON

TARGETOS=$(grep myarchname config.sh.COMMON | sed "s#.*'\(.*\)'.*#\1#")

sed -r -i config.sh.COMMON \
	-e "s#(install.*=')(/usr)/local(.*')#\1@DESTDIR@\2\3#g" \
	-e 's#'$TARGETOS'#@ARCH@#g'
