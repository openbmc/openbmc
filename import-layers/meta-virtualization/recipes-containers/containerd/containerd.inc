HOMEPAGE = "https://github.com/docker/containerd"
SUMMARY = "containerd is a daemon to control runC"
DESCRIPTION = "containerd is a daemon to control runC, built for performance and density. \
               containerd leverages runC's advanced features such as seccomp and user namespace \
               support as well as checkpoint and restore for cloning and live migration of containers."

# Apache-2.0 for containerd
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE.code;md5=aadc30f9c14d876ded7bedc0afd2d3d7"

SRC_URI += "file://containerd.service"

S = "${WORKDIR}/git"

PV = "${CONTAINERD_VERSION}+git${SRCREV}"

inherit go
inherit goarch

RRECOMMENDS_${PN} = "lxc docker"
CONTAINERD_PKG="github.com/containerd/containerd"

INSANE_SKIP_${PN} += "ldflags"

do_configure[noexec] = "1"

do_compile() {
	# Set GOPATH. See 'PACKAGERS.md'. Don't rely on
	# docker to download its dependencies but rather
	# use dependencies packaged independently.
	cd ${S}
	rm -rf .gopath
	mkdir -p .gopath/src/"$(dirname "${CONTAINERD_PKG}")"
	ln -sf ../../../.. .gopath/src/"${CONTAINERD_PKG}"

        export GOPATH="${WORKDIR}/git/"
        export GOROOT="${STAGING_LIBDIR_NATIVE}/${TARGET_SYS}/go"

	# Pass the needed cflags/ldflags so that cgo
	# can find the needed headers files and libraries
	export GOARCH="${TARGET_GOARCH}"
	export CGO_ENABLED="1"
	export CFLAGS=""
	export LDFLAGS=""
	export CGO_CFLAGS="${BUILDSDK_CFLAGS} --sysroot=${STAGING_DIR_TARGET}"
	export CGO_LDFLAGS="${BUILDSDK_LDFLAGS} --sysroot=${STAGING_DIR_TARGET}"
        export GO_GCFLAGS=""
	export CC_FOR_TARGET="${TARGET_PREFIX}gcc ${TARGET_CC_ARCH} --sysroot=${STAGING_DIR_TARGET}"
	export CXX_FOR_TARGET="${TARGET_PREFIX}g++ ${TARGET_CC_ARCH} --sysroot=${STAGING_DIR_TARGET}"

        oe_runmake static
}

# Note: disabled for now, since docker is launching containerd
# inherit systemd
# SYSTEMD_PACKAGES = "${@bb.utils.contains('DISTRO_FEATURES','systemd','${PN}','',d)}"
# SYSTEMD_SERVICE_${PN} = "${@bb.utils.contains('DISTRO_FEATURES','systemd','containerd.service','',d)}"

do_install() {
	mkdir -p ${D}/${bindir}

	cp ${S}/bin/containerd ${D}/${bindir}/containerd
	cp ${S}/bin/containerd-shim ${D}/${bindir}/containerd-shim
        cp ${S}/bin/ctr ${D}/${bindir}/containerd-ctr
	
	ln -sf containerd ${D}/${bindir}/docker-containerd
	ln -sf containerd-shim ${D}/${bindir}/docker-containerd-shim
	ln -sf containerd-ctr ${D}/${bindir}/docker-containerd-ctr

	if ${@bb.utils.contains('DISTRO_FEATURES','systemd','true','false',d)}; then
		install -d ${D}${systemd_unitdir}/system
		install -m 644 ${WORKDIR}/containerd.service ${D}/${systemd_unitdir}/system
	        # adjust from /usr/local/bin to /usr/bin/
		sed -e "s:/usr/local/bin/containerd:${bindir}/docker-containerd:g" -i ${D}/${systemd_unitdir}/system/containerd.service
	fi
}

FILES_${PN} += "${systemd_system_unitdir}/*"

INHIBIT_PACKAGE_STRIP = "1"
