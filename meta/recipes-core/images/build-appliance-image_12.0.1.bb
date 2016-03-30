SUMMARY = "An image containing the build system itself"
DESCRIPTION = "An image containing the build system that you can boot and run using either VMware Player or VMware Workstation."
HOMEPAGE = "http://www.yoctoproject.org/documentation/build-appliance"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COREBASE}/LICENSE;md5=4d92cd373abda3937c2bc47fbc49d690 \
                    file://${COREBASE}/meta/COPYING.MIT;md5=3da9cfbcb788c80a0384361b4de20420"

IMAGE_INSTALL = "packagegroup-core-boot packagegroup-core-ssh-openssh packagegroup-self-hosted"

IMAGE_FEATURES += "x11-base package-management splash"

# Ensure there's enough space to do a core-image-sato build, with rm_work enabled
IMAGE_ROOTFS_EXTRA_SPACE = "41943040"

# Do a quiet boot with limited console messages
APPEND += "rootfstype=ext4 quiet"

DEPENDS = "zip-native"
IMAGE_FSTYPES = "vmdk"

inherit core-image

SRCREV ?= "7fe17a2942ff03e2ec47d566fd5393f52b2eb736"
SRC_URI = "git://git.yoctoproject.org/poky;branch=jethro \
           file://Yocto_Build_Appliance.vmx \
           file://Yocto_Build_Appliance.vmxf \
          "

IMAGE_CMD_ext4_append () {
	# We don't need to reserve much space for root, 0.5% is more than enough
	tune2fs -m 0.5 ${DEPLOY_DIR_IMAGE}/${IMAGE_NAME}.rootfs.ext4
}

fakeroot do_populate_poky_src () {
	# Because fetch2's git's unpack uses -s cloneflag, the unpacked git repo
	# will become invalid in the target.
	rm -rf ${WORKDIR}/git/.git
	rm -f ${WORKDIR}/git/.gitignore

	cp -Rp ${WORKDIR}/git ${IMAGE_ROOTFS}/home/builder/poky

	mkdir -p ${IMAGE_ROOTFS}/home/builder/poky/build/conf
	mkdir -p ${IMAGE_ROOTFS}/home/builder/poky/build/downloads
	cp -RpL ${DL_DIR}/* ${IMAGE_ROOTFS}/home/builder/poky/build/downloads/

	# Remove the git2_* tarballs -- this is ok since we still have the git2/.
	rm -rf ${IMAGE_ROOTFS}/home/builder/poky/build/downloads/git2_*

	echo "/usr/bin" > ${IMAGE_ROOTFS}/home/builder/poky/build/pseudodone
	echo "INHERIT += \"rm_work\"" >> ${IMAGE_ROOTFS}/home/builder/poky/build/conf/auto.conf
	mkdir -p ${IMAGE_ROOTFS}/home/builder/pseudo
	echo "export PSEUDO_PREFIX=/usr" >> ${IMAGE_ROOTFS}/home/builder/.bashrc
	echo "export PSEUDO_LOCALSTATEDIR=/home/builder/pseudo" >> ${IMAGE_ROOTFS}/home/builder/.bashrc
	echo "export PSEUDO_LIBDIR=/usr/lib/pseudo/lib64" >> ${IMAGE_ROOTFS}/home/builder/.bashrc

	chown builder.builder ${IMAGE_ROOTFS}/home/builder/pseudo

	chown -R builder.builder  ${IMAGE_ROOTFS}/home/builder/poky

	# Allow builder to use sudo to setup tap/tun
	echo "builder ALL=(ALL) NOPASSWD: ALL" >> ${IMAGE_ROOTFS}/etc/sudoers

	# Use Clearlooks GTK+ theme
	mkdir -p ${IMAGE_ROOTFS}/etc/gtk-2.0
	echo 'gtk-theme-name = "Clearlooks"' > ${IMAGE_ROOTFS}/etc/gtk-2.0/gtkrc
}

IMAGE_PREPROCESS_COMMAND += "do_populate_poky_src; "

addtask rootfs after do_unpack

python () {
	# Ensure we run these usually noexec tasks
	d.delVarFlag("do_fetch", "noexec")
	d.delVarFlag("do_unpack", "noexec")
}

create_bundle_files () {
	cd ${WORKDIR}
	mkdir -p Yocto_Build_Appliance
	cp *.vmx* Yocto_Build_Appliance
	ln -sf ${DEPLOY_DIR_IMAGE}/${IMAGE_NAME}.vmdk Yocto_Build_Appliance/Yocto_Build_Appliance.vmdk
	zip -r ${DEPLOY_DIR_IMAGE}/Yocto_Build_Appliance-${DATETIME}.zip Yocto_Build_Appliance
	ln -sf Yocto_Build_Appliance-${DATETIME}.zip ${DEPLOY_DIR_IMAGE}/Yocto_Build_Appliance.zip 
}

python do_bundle_files() {
    bb.build.exec_func('create_bundle_files', d)
}

addtask bundle_files after do_vmimg before do_build
do_bundle_files[nostamp] = "1"
