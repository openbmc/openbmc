SUMMARY = "Linux Virtual Server administration Utility"
HOMEPAGE = "http://www.linux-vs.org/software/index.html"
DESCRIPTION = "Ipvsadm  is  used  to set up, maintain or inspect the virtual server \
table in the Linux kernel. The Linux Virtual  Server  can  be  used  to \
build  scalable  network  services  based  on  a cluster of two or more nodes. \
The active node of the cluster redirects service requests  to  a \
collection  of  server  hosts  that will actually perform the services. \
Supported features include two protocols (TCP and UDP),  three  packet-forwarding \
methods (NAT, tunneling, and direct routing), and eight load balancing algorithms \
(round robin, weighted round robin,  least-connec-tion, weighted least-connection, \
locality-based  least-connection, locality-based least-connection with replication, \
destination-hashing, and source-hashing)."
SECTION = "net"
LICENSE = "GPL-2.0"
LIC_FILES_CHKSUM = "file://README;beginline=40;endline=56;md5=b4e40069f15598d0e3fe2aa177e5ec77"

DEPENDS += "libnl popt"

SRC_URI = "https://mirrors.edge.kernel.org/pub/linux/utils/kernel/ipvsadm/${BP}.tar.xz \
        file://0001-Modify-the-Makefile-for-cross-compile.patch \
        file://0003-ipvsadm-remove-dependency-on-bash.patch \
        file://makefile-add-ldflags.patch \
"

SRC_URI[md5sum] = "f6d1707c5baf684b58fd33682d67871f"
SRC_URI[sha256sum] = "1a0a5e25b5a1226435d2fb76341656f83a710183aebb0d204db39c0ec3bedfdb"

UPSTREAM_CHECK_URI = "${KERNELORG_MIRROR}/linux/utils/kernel/ipvsadm"

do_compile() {
    oe_runmake \
    CC="${CC} -I${STAGING_INCDIR} -I${STAGING_INCDIR}/libnl3 -L${STAGING_LIBDIR}" \
    all
}

do_install() {
    sed -i -e "s;SBIN\t\t= \$(BUILD_ROOT)/sbin;SBIN\t\t= \$(BUILD_ROOT)$base_sbindir;" \
        -e "s;INIT\t\t= \$(BUILD_ROOT)/etc/rc.d/init.d;INIT\t\t= \$(BUILD_ROOT)${sysconfdir}/init.d;" \
        ${S}/Makefile
    oe_runmake 'BUILD_ROOT=${D}' install
}

inherit pkgconfig
