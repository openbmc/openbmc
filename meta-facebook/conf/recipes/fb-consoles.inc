# Default console to use if there is only one host.
OBMC_CONSOLE_HOST_TTY = "ttyS2"

# TTYs that are reserved for non-console purposes.
OBMC_CONSOLE_TTY_SKIP = "4"

# Calculate the TTY assignments for the hosts.
def fb_get_consoles(d):
    hosts = len(d.getVar('OBMC_HOST_INSTANCES', True).split())

    if hosts <= 1:
        return d.getVar('OBMC_CONSOLE_HOST_TTY', True)

    skipped = \
        [ int(i) for i in d.getVar('OBMC_CONSOLE_TTY_SKIP', True).split() ]
    consoles = \
        [ f"ttyS{i}" for i in sorted(set(range(0,16)).difference(skipped)) ]

    return " ".join(consoles[:hosts])
fb_get_consoles[vardeps] += "OBMC_HOST_INSTANCES OBMC_CONSOLE_HOST_TTY OBMC_CONSOLE_TTY_SKIP"

OBMC_CONSOLE_TTYS = "${@fb_get_consoles(d)}"
OBMC_CONSOLE_TTYS:fb-nohost = ""
OBMC_CONSOLE_TTYS[vardeps] += "fb_get_consoles"
