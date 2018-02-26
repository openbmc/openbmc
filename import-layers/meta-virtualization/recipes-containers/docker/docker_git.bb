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

SRCREV_docker = "e639a70fbe999d96354a5bcf560231b7b8aa935c"
SRCREV_libnetwork = "26addf43a5d925ff79d262dbbdb5344bc2b6e198"
SRCREV_cli = "a765218f1988e85b68aa3977f34893ec7b059a60"
SRC_URI = "\
	git://github.com/moby/moby.git;nobranch=1;name=docker \
	git://github.com/docker/libnetwork.git;branch=master;name=libnetwork;destsuffix=libnetwork \
	git://github.com/docker/cli;branch=master;name=cli;destsuffix=cli \
	file://docker.init \
	file://hi.Dockerfile \
	file://context-use-golang.org-x-net-pkg-until-we-move-to-go.patch \
	"

# Apache-2.0 for docker
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://src/import/LICENSE;md5=9740d093a080530b5c5c6573df9af45a"

GO_IMPORT = "import"

S = "${WORKDIR}/git"

DOCKER_VERSION = "17.06.0"
PV = "${DOCKER_VERSION}+git${SRCREV_docker}"

DEPENDS = " \
    go-cli \
    go-pty \
    go-context \
    go-mux \
    go-patricia \
    go-logrus \
    go-fsnotify \
    go-dbus \
    go-capability \
    go-systemd \
    btrfs-tools \
    sqlite3 \
    go-distribution \
    compose-file \
    go-connections \
    notary \
    grpc-go \
    "

PACKAGES =+ "${PN}-contrib"

DEPENDS_append_class-target = " lvm2"
RDEPENDS_${PN} = "curl aufs-util git util-linux iptables \
                  ${@bb.utils.contains('DISTRO_FEATURES','systemd','','cgroup-lite',d)} \
                 "
RDEPENDS_${PN} += "virtual/containerd virtual/runc"

RRECOMMENDS_${PN} = "kernel-module-dm-thin-pool kernel-module-nf-nat"
RSUGGESTS_${PN} = "lxc rt-tests"
DOCKER_PKG="github.com/docker/docker"

inherit systemd update-rc.d
inherit go
inherit goarch

do_configure[noexec] = "1"

do_compile() {
	# Set GOPATH. See 'PACKAGERS.md'. Don't rely on
	# docker to download its dependencies but rather
	# use dependencies packaged independently.
	cd ${S}/src/import
	rm -rf .gopath
	mkdir -p .gopath/src/"$(dirname "${DOCKER_PKG}")"
	ln -sf ../../../.. .gopath/src/"${DOCKER_PKG}"

	mkdir -p .gopath/src/github.com/docker
	ln -sf ${WORKDIR}/libnetwork .gopath/src/github.com/docker/libnetwork
	ln -sf ${WORKDIR}/cli .gopath/src/github.com/docker/cli

	export GOPATH="${S}/src/import/.gopath:${S}/src/import/vendor:${STAGING_DIR_TARGET}/${prefix}/local/go"
	export GOROOT="${STAGING_DIR_NATIVE}/${nonarch_libdir}/${HOST_SYS}/go"

	# Pass the needed cflags/ldflags so that cgo
	# can find the needed headers files and libraries
	export GOARCH=${TARGET_GOARCH}
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

	# build the proxy
	go build -o ${S}/src/import/docker-proxy github.com/docker/libnetwork/cmd/proxy

        # build the cli
	go build -o ${S}/src/import/bundles/latest/dynbinary-client/docker github.com/docker/cli/cmd/docker
}

SYSTEMD_PACKAGES = "${@bb.utils.contains('DISTRO_FEATURES','systemd','${PN}','',d)}"
SYSTEMD_SERVICE_${PN} = "${@bb.utils.contains('DISTRO_FEATURES','systemd','docker.service','',d)}"

SYSTEMD_AUTO_ENABLE_${PN} = "enable"

INITSCRIPT_PACKAGES += "${@bb.utils.contains('DISTRO_FEATURES','sysvinit','${PN}','',d)}"
INITSCRIPT_NAME_${PN} = "${@bb.utils.contains('DISTRO_FEATURES','sysvinit','docker.init','',d)}"
INITSCRIPT_PARAMS_${PN} = "defaults"

do_install() {
	mkdir -p ${D}/${bindir}
	cp ${S}/src/import/bundles/latest/dynbinary-client/docker ${D}/${bindir}/docker
	cp ${S}/src/import/bundles/latest/dynbinary-daemon/dockerd ${D}/${bindir}/dockerd
	cp ${S}/src/import/docker-proxy ${D}/${bindir}/docker-proxy

	if ${@bb.utils.contains('DISTRO_FEATURES','systemd','true','false',d)}; then
		install -d ${D}${systemd_unitdir}/system
		install -m 644 ${S}/src/import/contrib/init/systemd/docker.* ${D}/${systemd_unitdir}/system
		# replaces one copied from above with one that uses the local registry for a mirror
		install -m 644 ${S}/src/import/contrib/init/systemd/docker.service ${D}/${systemd_unitdir}/system
	else
		install -d ${D}${sysconfdir}/init.d
		install -m 0755 ${WORKDIR}/docker.init ${D}${sysconfdir}/init.d/docker.init
	fi

	mkdir -p ${D}${datadir}/docker/
	cp ${WORKDIR}/hi.Dockerfile ${D}${datadir}/docker/
	install -m 0755 ${S}/src/import/contrib/check-config.sh ${D}${datadir}/docker/
}

inherit useradd
USERADD_PACKAGES = "${PN}"
GROUPADD_PARAM_${PN} = "-r docker"

FILES_${PN} += "${systemd_unitdir}/system/*"

FILES_${PN}-contrib += "${datadir}/docker/check-config.sh"
RDEPENDS_${PN}-contrib += "bash"

# DO NOT STRIP docker
INHIBIT_PACKAGE_STRIP = "1"
INSANE_SKIP_${PN} += "ldflags"
