# Copyright (c) 2013 LG Electronics, Inc.
# Copyright (C) 2014 Intel Corp.

# This recipe creates packages for the bootchart2 system-wide profiler daemon 
# and related utilities. Depending on the images you're building, additional
# configuration may be needed in order to use it.
#
# Packages:
#   *   bootchart2 - The daemon itself.
#   *   pybootchartgui - Python program to visualize and display the data
#       collected by bootchart2 or compatible daemons such as the original 
#       bootchart.
#   *   bootchartd-stop-initscript - A SysV init script to stop data collection
#       when booting completes (see below for details.)
#
# While bootchart2 is designed to stop collecting data roughly when the boot 
# process completes, it is not exactly a stopwatch. It has a list of programs 
# which are supposed signify that the boot process has completed (for example,
# openbox or gnome-shell,) but it waits a full 20 seconds after such a program
# is launched before stopping itself, to collect additional data.
#
# If you are using a window manager or GUI which isn't included in bootchart2's
# default configuration file, you should write bbappend file to amend
# bootchartd.conf and add it to EXIT_PROC. An example of this is shown in this
# recipe, where the Matchbox window manager (used by Sato) is added.
#
# If you want data collection to end at a certain point exactly, you should
# arrange for the following command to be run:
#   bootchartd stop
# You might set this command to be launched by the desktop environment shipped
# on the image you're building after the other startup programs are complete.
# This will not incur the 20 second wait period and will cause bootchart2 to
# behave a bit more like a stopwatch. An example of this is shown in this 
# recipe, specifically the bootchartd-stop-initscript package, which stops data
# collection as the last action when switching to runlevels 2 through 5. You can
# add bootchartd-stop-initscript to IMAGE_INSTALL if you need to use it.
#
# Unless you're doing something special, if your image does not launch an X 
# window manager, you will need to add bootchartd-stop-initscript to your image.
#
# Bootchart2 can be started in two ways. Data collection can be initiated by
# running the following command:
#   bootchartd start
# However, for the most complete data, the bootchart2 developers recommend
# running it as PID 1. This can be done by adding the following to the kernel 
# command line parameters in the bootloader setup:
#   init=/sbin/bootchartd
# When invoked this way, bootchart2 will set itself up and then automatically 
# run /sbin/init. For example, when booting the default qemux86 image, one might
# use a command like this:
#   runqemu qemux86 bootparams="initcall_debug printk.time=y quiet \
#                               init=/sbin/bootchartd"
#
# Neither method is actually implemented here, choose what works for you.
#
# If you are building your image with systemd instead of SysV init, bootchart2
# includes systemd service files to begin collection automatically at boot and 
# end collection automatically 20 seconds after the boot process has completed.
# However, be aware that systemd tends to start bootchart2 relatively late into
# the boot process, so it's highly recommended to use bootchart2 as PID 1. If 
# you're using systemd and you wish to use another method to stop data
# collection at a time of your choosing, you may do so as long as you get to it
# before the 20 second timeout of the systemd service files. Also, you may write
# a bbappend to patch bootchart2-done.timer.in to increase or decrease the
# timeout. Decreasing it to 0 will make it behave like
# bootchartd-stop-initscript.
#
# By default, when data collection is stopped, a file named bootchart.tgz will
# be created in /var/log. If pybootchartgui is included in your image,
# bootchart.png will also be created at the same time. However, this results in
# a noticeable hitch or pause at boot time, which may not be what you want on an
# embedded device. So you may prefer to omit pybootchartgui from your image. In
# that case, copy bootchart.tgz over to your development system and generate
# bootchart.png there. To get pybootchartgui on your development system, you can
# either install it directly from some other source, or build bootchart2-native
# and find pybootchartgui in the native sysroot:
#    bitbake bootchart2-native
#   ./tmp/sysroots/x86_64-linux/usr/bin/pybootchartgui /path/to/bootchart.tgz
# Note that, whether installed on your build system or on your image, the 
# pybootchartgui provided by this recipe does not support the -i option. You 
# will need to install pybootchartgui by other means in order to run it in
# interactive mode.

SUMMARY = "Booting sequence and CPU,I/O usage monitor"
DESCRIPTION = "Monitors where the system spends its time at start, creating a graph of all processes, disk utilization, and wait time."
AUTHOR = "Wonhong Kwon <wonhong.kwon@lge.com>"
HOMEPAGE = "https://github.com/mmeeks/bootchart"
LICENSE = "GPL-3.0"
LIC_FILES_CHKSUM = "file://COPYING;md5=44ac4678311254db62edf8fd39cb8124"

UPSTREAM_CHECK_GITTAGREGEX = "(?P<pver>\d+\.\d+(\.\d+)*)"

SRC_URI = "git://github.com/xrmx/bootchart.git \
           file://bootchartd_stop.sh \
           file://0001-collector-Allocate-space-on-heap-for-chunks.patch \
           file://0001-bootchart2-support-usrmerge.patch \
          "

S = "${WORKDIR}/git"
SRCREV = "868a2afab9da34f32c007d773b77253c93104636"

inherit systemd update-rc.d python3native update-alternatives

ALTERNATIVE_${PN} = "bootchartd"
ALTERNATIVE_LINK_NAME[bootchartd] = "${base_sbindir}/bootchartd"
ALTERNATIVE_PRIORITY = "100"

# The only reason to build bootchart2-native is for a native pybootchartgui.
BBCLASSEXTEND = "native"

SYSTEMD_SERVICE_${PN} = "bootchart2.service bootchart2-done.service bootchart2-done.timer"

UPDATERCPN = "bootchartd-stop-initscript"
INITSCRIPT_NAME = "bootchartd_stop.sh"
INITSCRIPT_PARAMS = "start 99 2 3 4 5 ."

EXTRA_OEMAKE = 'BASE_SBINDIR="${base_sbindir}"'

do_compile_prepend () {
    export PY_LIBDIR="${libdir}/${PYTHON_DIR}"
    export BINDIR="${bindir}"
    export LIBDIR="${base_libdir}"
}

do_install () {
    install -d ${D}${sysconfdir} # needed for -native
    export PY_LIBDIR="${libdir}/${PYTHON_DIR}"
    export BINDIR="${bindir}"
    export DESTDIR="${D}"
    export LIBDIR="${base_libdir}"
    export PKGLIBDIR="${base_libdir}/bootchart"
    export SYSTEMD_UNIT_DIR="${systemd_unitdir}/system"

    oe_runmake install
    install -d ${D}${sysconfdir}/init.d
    install -m 0755 ${WORKDIR}/bootchartd_stop.sh ${D}${sysconfdir}/init.d

    echo 'EXIT_PROC="$EXIT_PROC matchbox-window-manager"' >> ${D}${sysconfdir}/bootchartd.conf

   # Use python 3 instead of python 2
   sed -i -e '1s,#!.*python.*,#!${USRBINPATH}/env python3,' ${D}${bindir}/pybootchartgui

    # The timestamps embedded in compressed man pages is not reproducible
    gzip -d ${D}${mandir}/man1/*.gz
}

PACKAGES =+ "pybootchartgui"
FILES_pybootchartgui += "${PYTHON_SITEPACKAGES_DIR}/pybootchartgui ${bindir}/pybootchartgui"
RDEPENDS_pybootchartgui = "python3-pycairo python3-compression python3-image python3-shell python3-compression python3-codecs"
RDEPENDS_${PN}_class-target += "${@bb.utils.contains('DISTRO_FEATURES', 'sysvinit', 'sysvinit-pidof', 'procps', d)}"
RDEPENDS_${PN}_class-target += "lsb-release"
DEPENDS_append_class-native = " python3-pycairo-native"

PACKAGES =+ "bootchartd-stop-initscript"
FILES_bootchartd-stop-initscript += "${sysconfdir}/init.d ${sysconfdir}/rc*.d"
RDEPENDS_bootchartd-stop-initscript = "${PN}"

FILES_${PN} += "${base_libdir}/bootchart/bootchart-collector"
FILES_${PN} += "${base_libdir}/bootchart/tmpfs"
FILES_${PN} += "${libdir}"
FILES_${PN}-doc += "${datadir}/docs"

RCONFLICTS_${PN} = "bootchart"
