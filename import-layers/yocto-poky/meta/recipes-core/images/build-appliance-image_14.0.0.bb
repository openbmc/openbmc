SUMMARY = "An image containing the build system itself"
DESCRIPTION = "An image containing the build system that you can boot and run using either VMware Player or VMware Workstation."
HOMEPAGE = "http://www.yoctoproject.org/documentation/build-appliance"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COREBASE}/LICENSE;md5=4d92cd373abda3937c2bc47fbc49d690 \
                    file://${COREBASE}/meta/COPYING.MIT;md5=3da9cfbcb788c80a0384361b4de20420"

IMAGE_INSTALL = "packagegroup-core-boot packagegroup-core-ssh-openssh packagegroup-self-hosted kernel-dev kernel-devsrc "

IMAGE_FEATURES += "x11-base package-management splash"

# Ensure there's enough space to do a core-image-sato build, with rm_work enabled
IMAGE_ROOTFS_EXTRA_SPACE = "41943040"

# Do a quiet boot with limited console messages
APPEND += "rootfstype=ext4 quiet"

DEPENDS = "zip-native"
IMAGE_FSTYPES = "vmdk"

inherit core-image module-base

SRCREV ?= "5f84d6545e6d7a2be8e603a1f4b1afae0dad0a9b"
SRC_URI = "git://git.yoctoproject.org/poky;branch=krogoth \
           file://Yocto_Build_Appliance.vmx \
           file://Yocto_Build_Appliance.vmxf \
           file://README_VirtualBox_Guest_Additions.txt \
          "
BA_INCLUDE_SOURCES ??= "0"

IMAGE_CMD_ext4_append () {
	# We don't need to reserve much space for root, 0.5% is more than enough
	tune2fs -m 0.5 ${DEPLOY_DIR_IMAGE}/${IMAGE_NAME}.rootfs.ext4
}

fakeroot do_populate_poky_src () {
	# Because fetch2's git's unpack uses -s cloneflag, the unpacked git repo
	# will become invalid in the target.
	rm -rf ${WORKDIR}/git/.git
	rm -f ${WORKDIR}/git/.gitignore

	cp -R ${WORKDIR}/git ${IMAGE_ROOTFS}/home/builder/poky

	mkdir -p ${IMAGE_ROOTFS}/home/builder/poky/build/conf
	mkdir -p ${IMAGE_ROOTFS}/home/builder/poky/build/downloads
	if [ ${BA_INCLUDE_SOURCES} != 0 ]; then
		cp -RpL ${DL_DIR}/* ${IMAGE_ROOTFS}/home/builder/poky/build/downloads/
		# Remove the git2_* tarballs -- this is ok since we still have the git2/.
		rm -rf ${IMAGE_ROOTFS}/home/builder/poky/build/downloads/git2_*
	fi

	# Place the README_VirtualBox_Guest_Additions file in builders home folder.
	cp ${WORKDIR}/README_VirtualBox_Guest_Additions.txt ${IMAGE_ROOTFS}/home/builder/

	# Create a symlink, needed for out-of-tree kernel modules build
	ln -snr ${IMAGE_ROOTFS}/usr/src/kernel ${IMAGE_ROOTFS}/lib/modules/${KERNEL_VERSION}/build

	echo "/usr/bin" > ${IMAGE_ROOTFS}/home/builder/poky/build/pseudodone
	echo "INHERIT += \"rm_work\"" >> ${IMAGE_ROOTFS}/home/builder/poky/build/conf/auto.conf
	mkdir -p ${IMAGE_ROOTFS}/home/builder/pseudo
	echo "export PSEUDO_PREFIX=/usr" >> ${IMAGE_ROOTFS}/home/builder/.bashrc
	echo "export PSEUDO_LOCALSTATEDIR=/home/builder/pseudo" >> ${IMAGE_ROOTFS}/home/builder/.bashrc
	echo "export PSEUDO_LIBDIR=/usr/lib/pseudo/lib64" >> ${IMAGE_ROOTFS}/home/builder/.bashrc

	# Also save (for reference only) the actual SRCREV used to create this image
	echo "export BA_SRCREV=${SRCREV}" >> ${IMAGE_ROOTFS}/home/builder/.bashrc
	echo "" >> ${IMAGE_ROOTFS}/home/builder/.bashrc
	echo "# If working behind a proxy and using the provided oe-git-proxy script" >> ${IMAGE_ROOTFS}/home/builder/.bashrc
	echo "# you need to set ALL_PROXY based on your proxy settings." >> ${IMAGE_ROOTFS}/home/builder/.bashrc
	echo "# Example ALL_PROXY values:" >> ${IMAGE_ROOTFS}/home/builder/.bashrc
	echo "# export ALL_PROXY=https://proxy.example.com:8080" >> ${IMAGE_ROOTFS}/home/builder/.bashrc
	echo "# export ALL_PROXY=socks://socks.example.com:1080" >> ${IMAGE_ROOTFS}/home/builder/.bashrc

	chown builder.builder ${IMAGE_ROOTFS}/home/builder/pseudo

	chown -R builder.builder ${IMAGE_ROOTFS}/home/builder/poky
	chmod -R ug+rw ${IMAGE_ROOTFS}/home/builder/poky

	# Assume we will need CDROM to install guest additions
	mkdir -p ${IMAGE_ROOTFS}/media/cdrom

	# Allow builder to use sudo
	echo "builder ALL=(ALL) NOPASSWD: ALL" >> ${IMAGE_ROOTFS}/etc/sudoers

	# Load tap/tun at startup
	ln -sr ${IMAGE_ROOTFS}/usr/sbin/iptables ${IMAGE_ROOTFS}/sbin/iptables
	echo "tun" >> ${IMAGE_ROOTFS}/etc/modules

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
create_bundle_files[vardepsexclude] = "DATETIME"

python do_bundle_files() {
    bb.build.exec_func('create_bundle_files', d)
}

addtask bundle_files after do_vmimg before do_build
do_bundle_files[nostamp] = "1"
