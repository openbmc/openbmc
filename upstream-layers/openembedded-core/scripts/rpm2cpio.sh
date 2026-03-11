#!/bin/sh -efu
# This file comes from rpm 4.x distribution

fatal() {
	echo "$*" >&2
	exit 1
}

pkg="$1"
[ -n "$pkg" ] && [ -e "$pkg" ] ||
	fatal "No package supplied"

_dd() {
	local o="$1"; shift
	dd if="$pkg" skip="$o" iflag=skip_bytes status=none $*
}

calcsize() {

	case "$(_dd $1 bs=4 count=1 | tr -d '\0')" in
		"$(printf '\216\255\350')"*) ;; # '\x8e\xad\xe8'
		*) fatal "File doesn't look like rpm: $pkg" ;;
	esac

	offset=$(($1 + 8))

	local i b b0 b1 b2 b3 b4 b5 b6 b7

	i=0
	while [ $i -lt 8 ]; do
		# add . to not loose \n
		# strip \0 as it gets dropped with warning otherwise
		b="$(_dd $(($offset + $i)) bs=1 count=1 | tr -d '\0' ; echo .)"
		b=${b%.}    # strip . again

		[ -z "$b" ] &&
			b="0" ||
			b="$(exec printf '%u\n' "'$b")"
		eval "b$i=\$b"
		i=$(($i + 1))
	done

	rsize=$((8 + ((($b0 << 24) + ($b1 << 16) + ($b2 << 8) + $b3) << 4) + ($b4 << 24) + ($b5 << 16) + ($b6 << 8) + $b7))
	offset=$(($offset + $rsize))
}

case "$(_dd 0 bs=4 count=1 | tr -d '\0')" in
	"$(printf '\355\253\356\333')"*) ;; # '\xed\xab\xee\xdb'
	*) fatal "File doesn't look like rpm: $pkg" ;;
esac

calcsize 96
sigsize=$rsize

calcsize $(($offset + (8 - ($sigsize % 8)) % 8))
hdrsize=$rsize

case "$(_dd $offset bs=2 count=1 | tr -d '\0')" in
	"$(printf '\102\132')") _dd $offset | bunzip2 ;; # '\x42\x5a'
	"$(printf '\037\213')") _dd $offset | gunzip  ;; # '\x1f\x8b'
	"$(printf '\375\067')") _dd $offset | xzcat   ;; # '\xfd\x37'
	"$(printf '\135')") _dd $offset | unlzma      ;; # '\x5d\x00'
	"$(printf '\050\265')") _dd $offset | unzstd  ;; # '\x28\xb5'
	*) fatal "Unrecognized payload compression format in rpm file: $pkg" ;;
esac
