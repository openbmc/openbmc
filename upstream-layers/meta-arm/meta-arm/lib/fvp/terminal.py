import collections
import pathlib
import os

import logging
import configparser
from typing import List, Optional


logger = logging.getLogger("Terminal")


def get_config_dir() -> pathlib.Path:
    value = os.environ.get("XDG_CONFIG_HOME")
    if value and os.path.isabs(value):
        return pathlib.Path(value)
    else:
        return pathlib.Path.home() / ".config"


def check_executable(*cmd) -> bool:
    import subprocess

    try:
        result = subprocess.run(
            cmd,
            stdout=subprocess.DEVNULL,
            stderr=subprocess.DEVNULL
        )

        exitcode = result.returncode

    except FileNotFoundError:
        exitcode = 127

    return exitcode == 0


def screen_is_ready(*, silent: bool = False) -> bool:
    log_print = (lambda *_args, **_kwargs: None) if silent else logger.error

    if not check_executable("screen", "--version"):
        log_print("--terminal screen requires screen to be available and runnable, but startup failed.")
        return False

    if not os.environ.get("STY"):
        log_print("--terminal screen requires runfvp to be started from a screen session.\n\tEnsure $STY is set in the environment.")
        return False

    return True


def tmux_is_ready(*, silent: bool = False) -> bool:
    log_print = (lambda *_args, **_kwargs: None) if silent else logger.error

    if not check_executable("tmux", "-V"):
        log_print("--terminal tmux requires tmux to be available and runnable, but startup failed.")
        return False

    return True


def is_display_available(log_print, terminal_name: str) -> bool:
    if "DISPLAY" not in os.environ and "WAYLAND_DISPLAY" not in os.environ:
        log_print(f"--terminal {terminal_name} requires a graphical display"
                  " but nor DISPLAY nor WAYLAND_DISPLAY is set.")
        return False
    return True


def gterm_is_ready(*, silent: bool = False) -> bool:
    log_print = (lambda *_args, **_kwargs: None) if silent else logger.error

    if not is_display_available(log_print, "gnome-terminal"):
        return False

    if not check_executable("gnome-terminal", "--version"):
        log_print("--terminal gnome-terminal requires gnome-terminal to be available and runnable, but startup failed.")
        return False

    return True


def xterm_is_ready(*, silent: bool = False) -> bool:
    log_print = (lambda *_args, **_kwargs: None) if silent else logger.error

    if not is_display_available(log_print, "xterm"):
        return False

    if not check_executable("xterm", "-version"):
        log_print("--terminal xterm requires xterm to be available and runnable, but startup failed.")
        return False

    return True


class Terminals:
    Terminal = collections.namedtuple("Terminal", ["priority", "name", "command", "is_ready"])

    def __init__(self):
        self.terminals = []

    def always_ready(self, *, silent: bool = False) -> bool:
        return True

    def add_terminal(self, priority, name, command, is_ready=None):
        if is_ready is None:
            is_ready = self.always_ready
        self.terminals.append(Terminals.Terminal(priority, name, command, is_ready))
        # Keep this list sorted by priority
        self.terminals.sort(reverse=True, key=lambda t: t.priority)
        self.name_map = {t.name: t for t in self.terminals}

    def configured_terminal(self) -> Optional[str]:
        config = configparser.ConfigParser()
        config.read(get_config_dir() / "runfvp.conf")
        return config.get("RunFVP", "Terminal", fallback=None)

    def preferred_terminal(self) -> str:
        preferred = self.configured_terminal()
        if preferred:
            return preferred

        for t in self.terminals:
            if t.command and t.is_ready(silent=True):
                return t.name
        return self.terminals[-1].name

    def all_terminals(self) -> List[str]:
        return self.name_map.keys()

    def available_terminals(self) -> List[str]:
        return [t for t in self.name_map if self.name_map[t].is_ready(silent=True)]

    def __getitem__(self, name: str):
        return self.name_map[name]


terminals = Terminals()
# TODO: option to switch between telnet and netcat
connect_command = "telnet localhost %port"
sty = os.environ.get("STY")
terminals.add_terminal(2, "screen", f'screen -S "{sty}" -X screen -t "{{name}} - %title" -L {connect_command}', screen_is_ready)
terminals.add_terminal(2, "tmux", f'tmux new-window -n "{{name}}" "{connect_command}"', tmux_is_ready)
terminals.add_terminal(2, "gnome-terminal", f'gnome-terminal --window --title "{{name}} - %title" --command "{connect_command}"', gterm_is_ready)
terminals.add_terminal(1, "xterm", f'xterm -title "{{name}} - %title" -e {connect_command}', xterm_is_ready)
terminals.add_terminal(0, "none", None)
