#!/bin/sh

[ $# -ge 1 ] || {
    cat > /dev/null
    exit 0
}

case $1 in
-R|--requires)
    shift
    grep "/usr/\(lib[^/]*\|share\)/python[^/]*/" >/dev/null && echo "python"
    exit 0
    ;;
esac

exit 0
