# Minimal/stub implementation of the XDG Base Directory specification.
# http://standards.freedesktop.org/basedir-spec/basedir-spec-latest.html
#
# Wayland needs XDG_RUNTIME_DIR, so set it to /tmp.  This isn't compliant with
# the specification (wrong mode, wrong owner) but it's mostly sufficient.
#
# In the ideal case where SystemD is booting and userspace is initiated by a
# SystemD user session this will have been set already, so don't overwrite it.

if [ -z "$XGD_RUNTIME_DIR" ]; then
	XDG_RUNTIME_DIR="/tmp"
        export XDG_RUNTIME_DIR
fi
