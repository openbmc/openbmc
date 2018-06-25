include runc.inc

# Note: this rev is before the required protocol field, update when all components
#       have been updated to match.
SRCREV_runc-docker = "4fc53a81fb7c994640722ac585fa9ca548971871"
SRC_URI = "git://github.com/opencontainers/runc;nobranch=1;name=runc-docker \
           file://0001-runc-Add-console-socket-dev-null.patch \
           file://0001-build-drop-recvtty-and-use-GOBUILDFLAGS.patch \
           file://0001-runc-docker-SIGUSR1-daemonize.patch \
          "

RUNC_VERSION = "1.0.0-rc5"
