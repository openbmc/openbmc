HOMEPAGE = "http://www.docker.com"
SUMMARY = "Linux container runtime"
DESCRIPTION = "Linux container runtime \
 Docker complements kernel namespacing with a high-level API which \
 operates at the process level. It runs unix processes with strong \
 guarantees of isolation and repeatability across servers. \
 . \
 Docker is a great building block for automating distributed systems: \
 large-scale web deployments, database clusters, continuous deployment \
 systems, private PaaS, service-oriented architectures, etc. \
 . \
 This package contains the daemon and client. Using docker.io is \
 officially supported on x86_64 and arm (32-bit) hosts. \
 Other architectures are considered experimental. \
 . \
 Also, note that kernel version 3.10 or above is required for proper \
 operation of the daemon process, and that any lower versions may have \
 subtle and/or glaring issues. \
 "

SRCREV = "7392c3b0ce0f9d3e918a321c66668c5d1ef4f689"
SRC_URI = "\
	git://github.com/docker/docker.git;nobranch=1 \
	file://docker.service \
	file://docker.init \
	file://hi.Dockerfile \
	"

# Apache-2.0 for docker
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=aadc30f9c14d876ded7bedc0afd2d3d7"

S = "${WORKDIR}/git"

DOCKER_VERSION = "1.12.5"
PV = "${DOCKER_VERSION}+git${SRCREV}"

DEPENDS = " \
    go-cross \
    go-cli \
    go-pty \
    go-context \
    go-mux \
    go-patricia \
    go-libtrust \
    go-logrus \
    go-fsnotify \
    go-dbus \
    go-capability \
    go-systemd \
    btrfs-tools \
    sqlite3 \
    go-distribution-digest \
    "

DEPENDS_append_class-target = "lvm2"
RDEPENDS_${PN} = "curl aufs-util git util-linux iptables \
                  ${@bb.utils.contains('DISTRO_FEATURES','systemd','','cgroup-lite',d)} \
                 "
RDEPENDS_${PN} += "containerd runc"
RRECOMMENDS_${PN} = "lxc docker-registry rt-tests"
RRECOMMENDS_${PN} += " kernel-module-dm-thin-pool kernel-module-nf-nat"
DOCKER_PKG="github.com/docker/docker"

do_configure[noexec] = "1"

do_compile() {
	case "${TARGET_ARCH}" in
		arm)
			GOARCH=arm
			case "${TUNE_PKGARCH}" in
				cortexa*)
					export GOARM=7
				;;
			esac
		;;
		aarch64)
			GOARCH=arm64
		;;
		i586|i686)
			GOARCH=386
		;;
		x86_64)
			GOARCH=amd64
		;;
		*)
			GOARCH="${TARGET_ARCH}"
		;;
	esac
	export GOARCH

	# Set GOPATH. See 'PACKAGERS.md'. Don't rely on
	# docker to download its dependencies but rather
	# use dependencies packaged independently.
	cd ${S}
	rm -rf .gopath
	mkdir -p .gopath/src/"$(dirname "${DOCKER_PKG}")"
	ln -sf ../../../.. .gopath/src/"${DOCKER_PKG}"
	export GOPATH="${S}/.gopath:${S}/vendor:${STAGING_DIR_TARGET}/${prefix}/local/go"
	export GOROOT="${STAGING_DIR_NATIVE}/${nonarch_libdir}/${HOST_SYS}/go"
	cd -

	# Pass the needed cflags/ldflags so that cgo
	# can find the needed headers files and libraries
	export CGO_ENABLED="1"
	export CGO_CFLAGS="${CFLAGS} --sysroot=${STAGING_DIR_TARGET}"
	export CGO_LDFLAGS="${LDFLAGS} --sysroot=${STAGING_DIR_TARGET}"
	# in order to exclude devicemapper and btrfs - https://github.com/docker/docker/issues/14056
	export DOCKER_BUILDTAGS='exclude_graphdriver_btrfs exclude_graphdriver_devicemapper'

	# this is the unsupported built structure
	# that doesn't rely on an existing docker
	# to build this:
	DOCKER_GITCOMMIT="${SRCREV}" \
	  ./hack/make.sh dynbinary
}

inherit systemd update-rc.d

SYSTEMD_PACKAGES = "${@bb.utils.contains('DISTRO_FEATURES','systemd','${PN}','',d)}"
SYSTEMD_SERVICE_${PN} = "${@bb.utils.contains('DISTRO_FEATURES','systemd','docker.service','',d)}"

INITSCRIPT_PACKAGES += "${@bb.utils.contains('DISTRO_FEATURES','sysvinit','${PN}','',d)}"
INITSCRIPT_NAME_${PN} = "${@bb.utils.contains('DISTRO_FEATURES','sysvinit','docker.init','',d)}"
INITSCRIPT_PARAMS_${PN} = "${OS_DEFAULT_INITSCRIPT_PARAMS}"

do_install() {
	mkdir -p ${D}/${bindir}
	cp ${S}/bundles/latest/dynbinary-client/docker ${D}/${bindir}/docker
	cp ${S}/bundles/latest/dynbinary-daemon/dockerd ${D}/${bindir}/dockerd
	cp ${S}/bundles/latest/dynbinary-daemon/docker-proxy ${D}/${bindir}/docker-proxy

	if ${@bb.utils.contains('DISTRO_FEATURES','systemd','true','false',d)}; then
		install -d ${D}${systemd_unitdir}/system
		install -m 644 ${S}/contrib/init/systemd/docker.* ${D}/${systemd_unitdir}/system
		# replaces one copied from above with one that uses the local registry for a mirror
		install -m 644 ${WORKDIR}/docker.service ${D}/${systemd_unitdir}/system
	else
		install -d ${D}${sysconfdir}/init.d
		install -m 0755 ${WORKDIR}/docker.init ${D}${sysconfdir}/init.d/docker.init
	fi

	mkdir -p ${D}/usr/share/docker/
	cp ${WORKDIR}/hi.Dockerfile ${D}/usr/share/docker/
}

inherit useradd
USERADD_PACKAGES = "${PN}"
GROUPADD_PARAM_${PN} = "-r docker"

FILES_${PN} += "/lib/systemd/system/*"

# DO NOT STRIP docker
INHIBIT_PACKAGE_STRIP = "1"
