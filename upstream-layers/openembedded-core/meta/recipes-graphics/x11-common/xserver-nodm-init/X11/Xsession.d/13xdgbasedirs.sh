# Minimal/stub implementation of the XDG Base Directory specification.
# http://standards.freedesktop.org/basedir-spec/basedir-spec-latest.html

# If the runtime directory hasn't been set already (for example by systemd,
# elogind, or pam) create a directory in TMPDIR.
if [ -z "$XDG_RUNTIME_DIR" ]; then
    XDG_RUNTIME_DIR=/run/user/$(id -u)
    export XDG_RUNTIME_DIR
fi

if [ -d "$XDG_RUNTIME_DIR" ]; then
    # If the directory exists, check the permissions and ownership
    if [ "$(stat -c %u-%a "$XDG_RUNTIME_DIR")" != "$(id -u)-700" ]; then
        echo "ERROR: $XDG_RUNTIME_DIR has incorrect permissions"
        exit 1
    fi
else
    mkdir --mode 0700 --parents "${XDG_RUNTIME_DIR}"
fi
