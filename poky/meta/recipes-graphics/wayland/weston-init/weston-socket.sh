#!/bin/sh

# set weston variables for use with global weston socket
global_socket="/run/wayland-0"
if [ -e "$global_socket" ]; then
	weston_group=$(stat -c "%G" "$global_socket")
	if [ "$(id -u)" = "0" ]; then
		export WAYLAND_DISPLAY="$global_socket"
	else
		case "$(groups "$USER")" in
			*"$weston_group"*)
				export WAYLAND_DISPLAY="$global_socket"
				;;
			*)
				;;
		esac
	fi
	unset weston_group
fi
unset global_socket
