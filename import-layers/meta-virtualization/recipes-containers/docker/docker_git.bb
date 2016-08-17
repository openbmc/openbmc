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
 This package contains the daemon and client. Using docker.io on non-amd64 \
 hosts is not supported at this time. Please be careful when using it \
 on anything besides amd64. \
 . \
 Also, note that kernel version 3.8 or above is required for proper \
 operation of the daemon process, and that any lower versions may have \
 subtle and/or glaring issues. \
 "

SRCREV = "76d6bc9a9f1690e16f3721ba165364688b626de2"
SRC_URI = "\
	git://github.com/docker/docker.git;nobranch=1 \
	file://docker.service \
	file://docker.init \
	file://hi.Dockerfile \
	file://disable_sha1sum_startup.patch \
	file://Bump-bolt-to-v1.1.0.patch \
	"

# Apache-2.0 for docker
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=cc2221abf0b96ea39dd68141b70f7937"

S = "${WORKDIR}/git"

DOCKER_VERSION = "1.9.0"
PV = "${DOCKER_VERSION}+git${SRCREV}"

DEPENDS = "go-cross \
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
RDEPENDS_${PN} = "curl aufs-util git cgroup-lite util-linux iptables"
RRECOMMENDS_${PN} = "lxc docker-registry rt-tests"
RRECOMMENDS_${PN} += " kernel-module-dm-thin-pool kernel-module-nf-nat"
DOCKER_PKG="github.com/docker/docker"

do_configure[noexec] = "1"

do_compile() {
	export GOARCH="${TARGET_ARCH}"
	# supported amd64, 386, arm arm64
	if [ "${TARGET_ARCH}" = "x86_64" ]; then
		export GOARCH="amd64"
	fi
	if [ "${TARGET_ARCH}" = "aarch64" ]; then
		export GOARCH="arm64"
	fi

	# Set GOPATH. See 'PACKAGERS.md'. Don't rely on
	# docker to download its dependencies but rather
	# use dependencies packaged independently.
	cd ${S}
	rm -rf .gopath
	mkdir -p .gopath/src/"$(dirname "${DOCKER_PKG}")"
	ln -sf ../../../.. .gopath/src/"${DOCKER_PKG}"
	export GOPATH="${S}/.gopath:${S}/vendor:${STAGING_DIR_TARGET}/${prefix}/local/go"
	cd -

	# Pass the needed cflags/ldflags so that cgo
	# can find the needed headers files and libraries
	export CGO_ENABLED="1"
	export CGO_CFLAGS="${BUILDSDK_CFLAGS} --sysroot=${STAGING_DIR_TARGET}"
	export CGO_LDFLAGS="${BUILDSDK_LDFLAGS} --sysroot=${STAGING_DIR_TARGET}"
	# in order to exclude devicemapper and btrfs - https://github.com/docker/docker/issues/14056
	export DOCKER_BUILDTAGS='exclude_graphdriver_btrfs exclude_graphdriver_devicemapper'

	# this is the unsupported built structure
	# that doesn't rely on an existing docker
	# to build this:
	DOCKER_GITCOMMIT="${SRCREV}" \
	  ./hack/make.sh dynbinary
}

inherit systemd update-rc.d

SYSTEMD_PACKAGES = "${@base_contains('DISTRO_FEATURES','systemd','${PN}','',d)}"
SYSTEMD_SERVICE_${PN} = "${@base_contains('DISTRO_FEATURES','systemd','docker.service','',d)}"

INITSCRIPT_PACKAGES += "${@base_contains('DISTRO_FEATURES','sysvinit','${PN}','',d)}"
INITSCRIPT_NAME_${PN} = "${@base_contains('DISTRO_FEATURES','sysvinit','docker.init','',d)}"
INITSCRIPT_PARAMS_${PN} = "${OS_DEFAULT_INITSCRIPT_PARAMS}"

do_install() {
	mkdir -p ${D}/${bindir}
	cp ${S}/bundles/${DOCKER_VERSION}/dynbinary/docker-${DOCKER_VERSION} \
	  ${D}/${bindir}/docker
	cp ${S}/bundles/${DOCKER_VERSION}/dynbinary/dockerinit-${DOCKER_VERSION} \
	  ${D}/${bindir}/dockerinit

	if ${@base_contains('DISTRO_FEATURES','systemd','true','false',d)}; then
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

# DO NOT STRIP docker and dockerinit!!!
#
# Reason:
# The "docker" package contains two binaries: "docker" and "dockerinit",
# which are both written in Go. The "dockerinit" package is built first,
# then its checksum is given to the build process compiling the "docker"
# binary. Hence the checksum of the unstripped "dockerinit" binary is hard
# coded into the "docker" binary. At runtime the "docker" binary invokes
# the "dockerinit" binary, but before doing that it ensures the checksum
# of "dockerinit" matches with the hard coded value.
#
INHIBIT_PACKAGE_STRIP = "1"
