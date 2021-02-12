SUMMARY = "An image containing the build system itself"
DESCRIPTION = "An image containing the build system that you can boot and run using either VirtualBox, VMware Player or VMware Workstation."
HOMEPAGE = "http://www.yoctoproject.org/documentation/build-appliance"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/COPYING.MIT;md5=3da9cfbcb788c80a0384361b4de20420"

IMAGE_INSTALL = "packagegroup-core-boot packagegroup-core-ssh-openssh packagegroup-self-hosted \
                 kernel-dev kernel-devsrc connman connman-plugin-ethernet dhcpcd \
                 tzdata python3-pip perl-misc"

IMAGE_FEATURES += "x11-base package-management splash"

QB_MEM = '${@bb.utils.contains("DISTRO_FEATURES", "opengl", "-m 512", "-m 256", d)}'

# Ensure there's enough space to do a core-image-sato build, with rm_work enabled
IMAGE_ROOTFS_EXTRA_SPACE = "41943040"

# Do a quiet boot with limited console messages
APPEND += "rootfstype=ext4 quiet"

DEPENDS = "zip-native python3-pip-native"
IMAGE_FSTYPES = "wic.vmdk wic.vhd wic.vhdx"

inherit core-image module-base setuptools3

SRCREV ?= "e56305dd709ae2af2da7a7599984b3ad18c4970f"
SRC_URI = "git://git.yoctoproject.org/poky \
           file://Yocto_Build_Appliance.vmx \
           file://Yocto_Build_Appliance.vmxf \
           file://README_VirtualBox_Guest_Additions.txt \
           file://README_VirtualBox_Toaster.txt \
          "
RECIPE_NO_UPDATE_REASON = "Recipe is recursive and handled as part of the release process"
BA_INCLUDE_SOURCES ??= "0"

IMAGE_CMD_ext4_append () {
	# We don't need to reserve much space for root, 0.5% is more than enough
	tune2fs -m 0.5 ${IMGDEPLOYDIR}/${IMAGE_NAME}.rootfs.ext4
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

	# Place the README_VirtualBox_Toaster file in builders home folder.
	cp ${WORKDIR}/README_VirtualBox_Toaster.txt ${IMAGE_ROOTFS}/home/builder/

	# Create a symlink, needed for out-of-tree kernel modules build
	if [ ! -e ${IMAGE_ROOTFS}/lib/modules/${KERNEL_VERSION}/build ]; then
		rm -f  ${IMAGE_ROOTFS}/lib/modules/${KERNEL_VERSION}/build
		lnr ${IMAGE_ROOTFS}${KERNEL_SRC_PATH} ${IMAGE_ROOTFS}/lib/modules/${KERNEL_VERSION}/build
	fi

	echo "INHERIT += \"rm_work\"" >> ${IMAGE_ROOTFS}/home/builder/poky/build/conf/auto.conf
	echo "export LC_ALL=en_US.utf8" >> ${IMAGE_ROOTFS}/home/builder/.bashrc

	# Also save (for reference only) the actual SRCREV used to create this image
	echo "export BA_SRCREV=${SRCREV}" >> ${IMAGE_ROOTFS}/home/builder/.bashrc
	echo "" >> ${IMAGE_ROOTFS}/home/builder/.bashrc
	echo 'export PATH=$PATH:/sbin' >> ${IMAGE_ROOTFS}/home/builder/.bashrc
	echo "" >> ${IMAGE_ROOTFS}/home/builder/.bashrc

	echo "# If working behind a proxy and using the provided oe-git-proxy script" >> ${IMAGE_ROOTFS}/home/builder/.bashrc
	echo "# you need to set ALL_PROXY based on your proxy settings." >> ${IMAGE_ROOTFS}/home/builder/.bashrc
	echo "# Example ALL_PROXY values:" >> ${IMAGE_ROOTFS}/home/builder/.bashrc
	echo "# export ALL_PROXY=https://proxy.example.com:8080" >> ${IMAGE_ROOTFS}/home/builder/.bashrc
	echo "# export ALL_PROXY=socks://socks.example.com:1080" >> ${IMAGE_ROOTFS}/home/builder/.bashrc

	chown -R builder:builder ${IMAGE_ROOTFS}/home/builder/poky
	chmod -R ug+rw ${IMAGE_ROOTFS}/home/builder/poky

	# Assume we will need CDROM to install guest additions
	mkdir -p ${IMAGE_ROOTFS}/media/cdrom

	# Allow builder to use sudo
	echo "builder ALL=(ALL) NOPASSWD: ALL" >> ${IMAGE_ROOTFS}/etc/sudoers

	# Load tap/tun at startup
	rm -f ${IMAGE_ROOTFS}/sbin/iptables
	lnr ${IMAGE_ROOTFS}/usr/sbin/iptables ${IMAGE_ROOTFS}/sbin/iptables
	echo "tun" >> ${IMAGE_ROOTFS}/etc/modules

	# Use Clearlooks GTK+ theme
	mkdir -p ${IMAGE_ROOTFS}/etc/gtk-2.0
	echo 'gtk-theme-name = "Clearlooks"' > ${IMAGE_ROOTFS}/etc/gtk-2.0/gtkrc

	# Install modules needed for toaster
	export STAGING_LIBDIR=${STAGING_LIBDIR_NATIVE}
	export STAGING_INCDIR=${STAGING_INCDIR_NATIVE}
	export HOME=${IMAGE_ROOTFS}/home/builder
	mkdir -p ${IMAGE_ROOTFS}/home/builder/.cache/pip
	pip3_install_params="--user -I -U -v -r ${IMAGE_ROOTFS}/home/builder/poky/bitbake/toaster-requirements.txt"
	if [ -n "${http_proxy}" ]; then
	   pip3_install_params="${pip3_install_params} --proxy ${http_proxy}"
	fi
	pip3 install ${pip3_install_params}
	chown -R builder:builder ${IMAGE_ROOTFS}/home/builder/.local
	chown -R builder:builder ${IMAGE_ROOTFS}/home/builder/.cache
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
	ln -sf ${IMGDEPLOYDIR}/${IMAGE_NAME}${IMAGE_NAME_SUFFIX}.wic.vmdk Yocto_Build_Appliance/Yocto_Build_Appliance.vmdk
	ln -sf ${IMGDEPLOYDIR}/${IMAGE_NAME}${IMAGE_NAME_SUFFIX}.wic.vhdx Yocto_Build_Appliance/Yocto_Build_Appliance.vhdx
	ln -sf ${IMGDEPLOYDIR}/${IMAGE_NAME}${IMAGE_NAME_SUFFIX}.wic.vhd Yocto_Build_Appliance/Yocto_Build_Appliance.vhd
	zip -r ${IMGDEPLOYDIR}/Yocto_Build_Appliance-${DATETIME}.zip Yocto_Build_Appliance
	ln -sf Yocto_Build_Appliance-${DATETIME}.zip ${IMGDEPLOYDIR}/Yocto_Build_Appliance.zip
}
create_bundle_files[vardepsexclude] = "DATETIME"

python do_bundle_files() {
    bb.build.exec_func('create_bundle_files', d)
}

addtask bundle_files after do_image_wic before do_image_complete
