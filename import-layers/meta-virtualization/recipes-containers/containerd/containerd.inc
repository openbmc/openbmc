HOMEPAGE = "https://github.com/docker/containerd"
SUMMARY = "containerd is a daemon to control runC"
DESCRIPTION = "containerd is a daemon to control runC, built for performance and density. \
               containerd leverages runC's advanced features such as seccomp and user namespace \
               support as well as checkpoint and restore for cloning and live migration of containers."

# Apache-2.0 for containerd
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://src/import/LICENSE.code;md5=aadc30f9c14d876ded7bedc0afd2d3d7"

SRC_URI += "file://containerd.service"

S = "${WORKDIR}/git"

PV = "${CONTAINERD_VERSION}+git${SRCREV}"

inherit go
inherit goarch

GO_IMPORT = "import"

RRECOMMENDS_${PN} = "lxc docker"
CONTAINERD_PKG="github.com/containerd/containerd"

INSANE_SKIP_${PN} += "ldflags"

do_configure[noexec] = "1"

do_compile() {
    export GOARCH="${TARGET_GOARCH}"

    # link fixups for compilation
    rm -f ${S}/src/import/vendor/src
    ln -sf ./ ${S}/src/import/vendor/src

    mkdir -p ${S}/src/import/vendor/src/github.com/containerd/containerd/
    # without this, the stress test parts of the build fail
    cp ${S}/src/import/*.go ${S}/src/import/vendor/src/github.com/containerd/containerd

    for c in content errdefs fs images mount snapshots linux api runtimes defaults progress \
		     protobuf reference diff platforms runtime remotes version archive dialer gc metadata \
		     metrics filters identifiers labels leases plugin server services \
		     cmd cio containers namespaces oci events log reaper sys rootfs; do
	ln -sfn ${S}/src/import/${c} ${S}/src/import/vendor/github.com/containerd/containerd/${c}
    done

    export GOPATH="${S}/src/import/.gopath:${S}/src/import/vendor:${STAGING_DIR_TARGET}/${prefix}/local/go"
    export GOROOT="${STAGING_DIR_NATIVE}/${nonarch_libdir}/${HOST_SYS}/go"

    # Pass the needed cflags/ldflags so that cgo
    # can find the needed headers files and libraries
    export CGO_ENABLED="1"
    export CGO_CFLAGS="${CFLAGS} --sysroot=${STAGING_DIR_TARGET}"
    export CGO_LDFLAGS="${LDFLAGS} --sysroot=${STAGING_DIR_TARGET}"
    export BUILDTAGS="no_btrfs static_build netgo"
    export CFLAGS="${CFLAGS}"
    export LDFLAGS="${LDFLAGS}"

    cd ${S}/src/import
    oe_runmake binaries
}

# Note: disabled for now, since docker is launching containerd
# inherit systemd
# SYSTEMD_PACKAGES = "${@bb.utils.contains('DISTRO_FEATURES','systemd','${PN}','',d)}"
# SYSTEMD_SERVICE_${PN} = "${@bb.utils.contains('DISTRO_FEATURES','systemd','containerd.service','',d)}"

do_install() {
	mkdir -p ${D}/${bindir}

	cp ${S}/src/import/bin/containerd ${D}/${bindir}/containerd
	cp ${S}/src/import/bin/containerd-shim ${D}/${bindir}/containerd-shim
	cp ${S}/src/import/bin/ctr ${D}/${bindir}/containerd-ctr
	
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
INSANE_SKIP_${PN} += "ldflags already-stripped"
