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
LIC_FILES_CHKSUM = "file://README;beginline=40;endline=56;md5=a54cba37b64924aa5008881607942892"

DEPENDS += "libnl popt"

SRC_URI = "http://www.linuxvirtualserver.org/software/kernel-2.6/${BP}.tar.gz \
        file://0001-Modify-the-Makefile-for-cross-compile.patch \
        file://0002-Replace-nl_handle-to-nl_sock.patch \
        file://0003-ipvsadm-remove-dependency-on-bash.patch \
        file://makefile-add-ldflags.patch \
"

SRC_URI[md5sum] = "eac3ba3f62cd4dea2da353aeddd353a8"
SRC_URI[sha256sum] = "6d6c46fecb1c532a892616b4445c73b71730e8790d5630f60269fd9cbee0eb2d"

do_compile() {
    oe_runmake \
    CC="${CC} -I${STAGING_INCDIR} -I${STAGING_INCDIR}/libnl3 -L${STAGING_LIBDIR}" \
    all
}

do_install() {
    oe_runmake 'BUILD_ROOT=${D}' install
}
