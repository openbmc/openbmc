# Yocto ADT Installer bb file
#
# Copyright 2010-2012 by Intel Corp.
#
# Permission is hereby granted, free of charge, to any person obtaining a copy 
# of this software and associated documentation files (the "Software"), to deal 
# in the Software without restriction, including without limitation the rights 
# to use, copy, modify, merge, publish, distribute, sublicense, and/or sell 
# copies of the Software, and to permit persons to whom the Software is 
# furnished to do so, subject to the following conditions:

# The above copyright notice and this permission notice shall be included in 
# all copies or substantial portions of the Software.

# THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR 
# IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, 
# FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE 
# AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER 
# LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, 
# OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN 
# THE SOFTWARE.


SUMMARY = "Application Development Toolkit"
DESCRIPTION = "Creates the Application Development Toolkit (ADT) installer tarball"
HOMEPAGE = "http://www.yoctoproject.org/tools-resources/projects/application-development-toolkit-adt"
LIC_FILES_CHKSUM = "file://${COREBASE}/LICENSE;md5=4d92cd373abda3937c2bc47fbc49d690 \
                    file://${COREBASE}/meta/COPYING.MIT;md5=3da9cfbcb788c80a0384361b4de20420"
LICENSE = "MIT"

PACKAGES = ""
INHIBIT_DEFAULT_DEPS = "1"

PR = "r11"

ADT_DEPLOY = "${DEPLOY_DIR}/sdk/"
ADT_DIR = "${WORKDIR}/adt-installer/"
S = "${WORKDIR}/opkg-${PV}"

PV = "0.3.0"
SRC_URI = "http://downloads.yoctoproject.org/releases/opkg/opkg-${PV}.tar.gz \
           file://wget_cache.patch \
           file://adt_installer \
           file://scripts/adt_installer_internal \
           file://scripts/util \
           file://scripts/data_define \
           file://scripts/extract_rootfs \
           file://adt_installer.conf \
           file://opkg/conf/opkg-sdk-x86_64.conf \
           file://opkg/conf/opkg-sdk-i686.conf \
	  "

SRC_URI[md5sum] = "3412cdc71d78b98facc84b19331ec64e"
SRC_URI[sha256sum] = "7f735d1cdb8ef3718fb0f9fba44ca0d9a5c90d3a7f014f37a6d2f9474f54988f"

ADTREPO ?= "http://adtrepo.yoctoproject.org/${SDK_VERSION}"

# This recipe makes no sense as a multilib
MULTILIBS = ""

do_populate_adt[umask] = "022"

fakeroot do_populate_adt () {
	cd ${WORKDIR}
	mkdir -p ${ADT_DEPLOY}
	rm -f ${ADT_DEPLOY}/adt_installer.tar.bz2
	rm -rf ${ADT_DIR}
	mkdir -p ${ADT_DIR}/opkg/build
	cp -r opkg ${ADT_DIR}/
	sed -i -e 's#ADTREPO_URL#${ADTREPO}#' ${ADT_DIR}/opkg/conf/*.conf
	cp -r opkg-${PV} ${ADT_DIR}/opkg/build/
	mv ${ADT_DIR}/opkg/build/opkg-${PV} ${ADT_DIR}/opkg/build/opkg-svn
	rm -rf ${ADT_DIR}/opkg/build/opkg-svn/patches ${ADT_DIR}/opkg/build/opkg-svn/.pc
	cp -r scripts ${ADT_DIR}/
	cp adt_installer ${ADT_DIR}
	cp adt_installer.conf ${ADT_DIR}
	sed -i -e 's#ADTREPO#${ADTREPO}#' ${ADT_DIR}/adt_installer.conf
	echo 'SDK_VENDOR=${SDK_VENDOR}' >> ${ADT_DIR}/scripts/data_define
	echo 'DEFAULT_INSTALL_FOLDER=${SDKPATH}' >> ${ADT_DIR}/scripts/data_define
	cp ${COREBASE}/scripts/relocate_sdk.py ${ADT_DIR}/scripts/
	tar cfj adt_installer.tar.bz2 adt-installer
	cp ${WORKDIR}/adt_installer.tar.bz2 ${ADT_DEPLOY}
}

do_configure[noexec] = "1"
do_compile[noexec] = "1"
do_package[noexec] = "1"
do_packagedata[noexec] = "1"
do_package_write_ipk[noexec] = "1"
do_package_write_rpm[noexec] = "1"
do_package_write_deb[noexec] = "1"
do_poplulate_sysroot[noexec] = "1"

addtask populate_adt before do_build after do_install
