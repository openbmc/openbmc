SUMMARY = "A System and service manager"
HOMEPAGE = "http://www.freedesktop.org/wiki/Software/systemd"

DESCRIPTION = "systemd is a system and service manager for Linux, compatible with \
SysV and LSB init scripts. systemd provides aggressive parallelization \
capabilities, uses socket and D-Bus activation for starting services, \
offers on-demand starting of daemons, keeps track of processes using \
Linux cgroups, supports snapshotting and restoring of the system \
state, maintains mount and automount points and implements an \
elaborate transactional dependency-based service control logic. It can \
work as a drop-in replacement for sysvinit."

LICENSE = "GPL-2.0-only & LGPL-2.1-or-later"
LICENSE:libsystemd = "LGPL-2.1-or-later"
LIC_FILES_CHKSUM = "file://LICENSE.GPL2;md5=c09786363500a9acc29b147e6e72d2c6 \
                    file://LICENSE.LGPL2.1;md5=be0aaf4a380f73f7e00b420a007368f2"

SRCREV = "32bc9ae9f58aed3fa06e111b55fc157a3fe1322b"
SRCBRANCH = "v259-stable"
SRC_URI = "git://github.com/systemd/systemd.git;protocol=https;branch=${SRCBRANCH};tag=v${PV}"

CVE_PRODUCT = "systemd"
