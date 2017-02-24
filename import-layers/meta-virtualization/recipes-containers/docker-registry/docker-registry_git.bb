HOMEPAGE = "https://github.com/docker/docker-registry"
SUMMARY = "Registry server for Docker"
DESCRIPTION = "\
 This is the classic python docker-registry. \
 . \
 hosting/delivering of repositories and images \
 "

SRCREV = "fd8c0c114985547b69088e0f1526e58bfe2ff914"
SRC_URI = "\
	git://github.com/docker/docker-registry.git \
	file://docker-registry.conf \
	file://docker-registry.service \
	file://config.yml \
	file://change_sqlalchemy_rqt.patch \
	"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=35e8e5305c1b7b4a5761f9de5d44e5f4"

S = "${WORKDIR}/git"

PV = "0.9.1+git${SRCREV}"

RDEPENDS_${PN} += "\
  docker \
  gunicorn (>= 19.1.1) \
  python-pip \
  python-distribute \
  python-m2crypto (>= 0.22.3) \
  python-pyyaml (>= 3.11) \
  python-flask (>= 0.10.1) \
  python-gevent (>= 1.0.1) \
  python-requests \
  python-sqlalchemy (>= 0.9.4) \
  python-blinker (>= 1.3) \
  python-backports-lzma (>= 0.0.3) \
  python-flask-cors (>= 1.10.3) \
  python-bugsnag (>= 2.0.2) \
  python-docker-registry-core (>= 2.0.3) \
  python-newrelic (>= 2.22.0.19) \
  python-itsdangerous (>= 0.21) \
  python-jinja2 (>= 2.4) \
  python-werkzeug (>= 0.7) \
  python-simplejson (>= 3.6.2) \
  python-redis (>= 2.10.3) \
  python-boto (>= 2.34.0) \
  python-webob \
  "
# OFFICIAL REQ:
# docker-registry-core>=2,<3
# blinker==1.3
# backports.lzma==0.0.3,!=0.0.4

# Flask==0.10.1
# gevent==1.0.1
# gunicorn==19.1.1
# PyYAML==3.11
# requests==2.3.0
# M2Crypto==0.22.3
# sqlalchemy==0.9.4
# setuptools==5.8
# 
# [bugsnag]
# bugsnag>=2.0,<2.1
# 
# [cors]
# Flask-cors>=1.8,<2.0
# 
# [newrelic]
# newrelic>=2.22,<2.23


inherit setuptools systemd

SYSTEMD_PACKAGES = "${@bb.utils.contains('DISTRO_FEATURES','systemd','${PN}','',d)}"
SYSTEMD_SERVICE_${PN} = "${@bb.utils.contains('DISTRO_FEATURES','systemd','docker-registry.service','',d)}"

do_install_append() {
	mkdir -p ${D}/etc/default/
	cp ${WORKDIR}/docker-registry.conf ${D}/etc/default/docker-registry

	if ${@bb.utils.contains('DISTRO_FEATURES','systemd','true','false',d)}; then
		install -d ${D}${systemd_unitdir}/system
		install -m 644 ${WORKDIR}/docker-registry.service ${D}/${systemd_unitdir}/system
		sed -i "s|#WORKDIR#|${PYTHON_SITEPACKAGES_DIR}/docker_registry|" ${D}/${systemd_unitdir}/system/docker-registry.service
	fi
	# based on config_mirror.yml - uses /var/docker-registry instead of /tmp for files
	install ${WORKDIR}/config.yml ${D}/etc/docker-registry.yml
	mkdir -p ${D}/var/docker-registry
}

FILES_${PN} += "/etc/default /var/docker-registry /etc/ /etc/default/volatiles"
