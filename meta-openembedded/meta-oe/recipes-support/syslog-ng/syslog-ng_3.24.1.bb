require syslog-ng.inc

# We only want to add stuff we need to the defaults provided in syslog-ng.inc.
#
SRC_URI += " \
           file://fix-config-libnet.patch \
           file://fix-invalid-ownership.patch \
           file://syslog-ng.service-the-syslog-ng-service.patch \
           file://0001-syslog-ng-fix-segment-fault-during-service-start.patch \
           file://shebang.patch \
           file://syslog-ng-tmp.conf \
           "

SRC_URI[md5sum] = "ef9de066793f7358af7312b964ac0450"
SRC_URI[sha256sum] = "d4d0a0357b452be96b69d6f741129275530d8f0451e35adc408ad5635059fa3d"
