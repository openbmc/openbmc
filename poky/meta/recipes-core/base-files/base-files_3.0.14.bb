SUMMARY = "Miscellaneous files for the base system"
DESCRIPTION = "The base-files package creates the basic system directory structure and provides a small set of key configuration files for the system."
SECTION = "base"
PR = "r89"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://licenses/GPL-2;md5=94d55d512a9ba36caa9b7df079bae19f"
# Removed all license related tasks in this recipe as license.bbclass 
# now deals with this. In order to get accurate licensing on to the image:
# Set COPY_LIC_MANIFEST to just copy just the license.manifest to the image
# For the manifest and the license text for each package:
# Set COPY_LIC_MANIFEST and COPY_LIC_DIRS

SRC_URI = "file://rotation \
           file://nsswitch.conf \
           file://motd \
           file://host.conf \
           file://profile \
           file://shells \
           file://fstab \
           file://filesystems \
           file://issue.net \
           file://issue \
           file://usbd \
           file://share/dot.bashrc \
           file://share/dot.profile \
           file://licenses/GPL-2 \
           "
S = "${WORKDIR}"

INHIBIT_DEFAULT_DEPS = "1"

docdir_append = "/${P}"
dirs1777 = "/tmp ${localstatedir}/volatile/tmp"
dirs2775 = ""
dirs555 = "/sys /proc"
dirs755 = "/boot /dev ${base_bindir} ${base_sbindir} ${base_libdir} \
           ${sysconfdir} ${sysconfdir}/default \
           ${sysconfdir}/skel ${nonarch_base_libdir} /mnt ${ROOT_HOME} /run \
           ${prefix} ${bindir} ${docdir} /usr/games ${includedir} \
           ${libdir} ${sbindir} ${datadir} \
           ${datadir}/common-licenses ${datadir}/dict ${infodir} \
           ${mandir} ${datadir}/misc ${localstatedir} \
           ${localstatedir}/backups ${localstatedir}/lib \
           ${localstatedir}/lib/misc ${localstatedir}/spool \
           ${localstatedir}/volatile \
           ${localstatedir}/${@'volatile/' if oe.types.boolean('${VOLATILE_LOG_DIR}') else ''}log \
           /home ${prefix}/src ${localstatedir}/local \
           /media"

dirs755-lsb = "/srv  \
               ${prefix}/local ${prefix}/local/bin ${prefix}/local/games \
               ${prefix}/local/include ${prefix}/local/lib ${prefix}/local/sbin \
               ${prefix}/local/share ${prefix}/local/src \
               ${prefix}/lib/locale"
dirs2775-lsb = "/var/mail"

volatiles = "${@'log' if oe.types.boolean('${VOLATILE_LOG_DIR}') else ''} tmp"
conffiles = "${sysconfdir}/debian_version ${sysconfdir}/host.conf \
             ${sysconfdir}/issue /${sysconfdir}/issue.net \
             ${sysconfdir}/nsswitch.conf ${sysconfdir}/profile \
             ${sysconfdir}/default"

# By default the hostname is the machine name. If the hostname is unset then a
# /etc/hostname file isn't written, suitable for environments with dynamic
# hostnames.
#
# The hostname can be changed outside of this recipe by using
# hostname_pn-base-files = "my-host-name".
hostname = "${MACHINE}"

BASEFILESISSUEINSTALL ?= "do_install_basefilesissue"

# In previous versions of base-files, /run was a softlink to /var/run and the
# directory was located in /var/volatlie/run.  Also, /var/lock was a softlink
# to /var/volatile/lock which is where the real directory was located.  Now,
# /run and /run/lock are the real directories.  If we are upgrading, we may
# need to remove the symbolic links first before we create the directories.
# Otherwise the directory creation will fail and we will have circular symbolic
# links.
# 
pkg_preinst_${PN} () {
    #!/bin/sh -e
    if [ x"$D" = "x" ]; then
        if [ -h "/var/lock" ]; then
            # Remove the symbolic link
            rm -f /var/lock
        fi

        if [ -h "/run" ]; then
            # Remove the symbolic link
            rm -f /run
        fi
    fi     
}

do_install () {
	for d in ${dirs555}; do
		install -m 0555 -d ${D}$d
	done
	for d in ${dirs755}; do
		install -m 0755 -d ${D}$d
	done
	for d in ${dirs1777}; do
		install -m 1777 -d ${D}$d
	done
	for d in ${dirs2775}; do
		install -m 2775 -d ${D}$d
	done
	for d in ${volatiles}; do
		ln -sf volatile/$d ${D}${localstatedir}/$d
	done

	ln -snf ../run ${D}${localstatedir}/run
	ln -snf ../run/lock ${D}${localstatedir}/lock

	${BASEFILESISSUEINSTALL}

	rotation=`cat ${WORKDIR}/rotation`
	if [ "$rotation" != "0" ]; then
 		install -m 0644 ${WORKDIR}/rotation ${D}${sysconfdir}/rotation
	fi

	install -m 0644 ${WORKDIR}/fstab ${D}${sysconfdir}/fstab
	install -m 0644 ${WORKDIR}/filesystems ${D}${sysconfdir}/filesystems
	install -m 0644 ${WORKDIR}/usbd ${D}${sysconfdir}/default/usbd
	install -m 0644 ${WORKDIR}/profile ${D}${sysconfdir}/profile
	sed -i 's#ROOTHOME#${ROOT_HOME}#' ${D}${sysconfdir}/profile
	install -m 0644 ${WORKDIR}/shells ${D}${sysconfdir}/shells
	install -m 0755 ${WORKDIR}/share/dot.profile ${D}${sysconfdir}/skel/.profile
	install -m 0755 ${WORKDIR}/share/dot.bashrc ${D}${sysconfdir}/skel/.bashrc
	install -m 0644 ${WORKDIR}/nsswitch.conf ${D}${sysconfdir}/nsswitch.conf
	install -m 0644 ${WORKDIR}/host.conf ${D}${sysconfdir}/host.conf
	install -m 0644 ${WORKDIR}/motd ${D}${sysconfdir}/motd

	ln -sf /proc/mounts ${D}${sysconfdir}/mtab
}

DISTRO_VERSION[vardepsexclude] += "DATE"
do_install_basefilesissue () {
	if [ "${hostname}" ]; then
		echo ${hostname} > ${D}${sysconfdir}/hostname
	fi

	install -m 644 ${WORKDIR}/issue*  ${D}${sysconfdir}
        if [ -n "${DISTRO_NAME}" ]; then
		printf "${DISTRO_NAME} " >> ${D}${sysconfdir}/issue
		printf "${DISTRO_NAME} " >> ${D}${sysconfdir}/issue.net
		if [ -n "${DISTRO_VERSION}" ]; then
			distro_version_nodate=${@'${DISTRO_VERSION}'.replace('snapshot-${DATE}','snapshot').replace('${DATE}','')}
			printf "%s " $distro_version_nodate >> ${D}${sysconfdir}/issue
			printf "%s " $distro_version_nodate >> ${D}${sysconfdir}/issue.net
		fi
		printf "\\\n \\\l\n" >> ${D}${sysconfdir}/issue
		echo >> ${D}${sysconfdir}/issue
		echo "%h"    >> ${D}${sysconfdir}/issue.net
		echo >> ${D}${sysconfdir}/issue.net
 	fi
}
do_install_basefilesissue[vardepsexclude] += "DATE"

do_install_append_linuxstdbase() {
	for d in ${dirs755-lsb}; do
                install -m 0755 -d ${D}$d
        done

	for d in ${dirs2775-lsb}; do
                install -m 2775 -d ${D}$d
        done
}

SYSROOT_DIRS += "${sysconfdir}/skel"

PACKAGES = "${PN}-doc ${PN} ${PN}-dev ${PN}-dbg"
FILES_${PN} = "/"
FILES_${PN}-doc = "${docdir} ${datadir}/common-licenses"

PACKAGE_ARCH = "${MACHINE_ARCH}"

CONFFILES_${PN} = "${sysconfdir}/fstab ${@['', '${sysconfdir}/hostname'][(d.getVar('hostname') != '')]} ${sysconfdir}/shells"
CONFFILES_${PN} += "${sysconfdir}/motd ${sysconfdir}/nsswitch.conf ${sysconfdir}/profile"
