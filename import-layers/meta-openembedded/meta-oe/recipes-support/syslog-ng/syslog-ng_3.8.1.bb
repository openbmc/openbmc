require syslog-ng.inc

SRC_URI += " \
    file://fix-config-libnet.patch \
    file://fix-invalid-ownership.patch \
    file://Fix-the-memory-leak-problem-when-HAVE_ENVIRON-defined.patch \
    file://syslog-ng.service-the-syslog-ng-service.patch \
"

SRC_URI[md5sum] = "acf14563cf5ce435db8db35486ce66af"
SRC_URI[sha256sum] = "84b081f6e5f98cbc52052e342bcfdc5de5fe0ebe9f5ec32fe9eaec5759224cc5"
