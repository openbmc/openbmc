import shutil
import collections
import pathlib
import os

from typing import List, Optional


def get_config_dir() -> pathlib.Path:
    value = os.environ.get("XDG_CONFIG_HOME")
    if value and os.path.isabs(value):
        return pathlib.Path(value)
    else:
        return pathlib.Path.home() / ".config"

class Terminals:
    Terminal = collections.namedtuple("Terminal", ["priority", "name", "command"])

    def __init__(self):
        self.terminals = []

    def add_terminal(self, priority, name, command):
        self.terminals.append(Terminals.Terminal(priority, name, command))
        # Keep this list sorted by priority
        self.terminals.sort(reverse=True, key=lambda t: t.priority)
        self.name_map = {t.name: t for t in self.terminals}

    def configured_terminal(self) -> Optional[str]:
        import configparser

        config = configparser.ConfigParser()
        config.read(get_config_dir() / "runfvp.conf")
        return config.get("RunFVP", "Terminal", fallback=None)

    def preferred_terminal(self) -> str:
        import shlex

        preferred = self.configured_terminal()
        if preferred:
            return preferred

        for t in self.terminals:
            if t.command and shutil.which(shlex.split(t.command)[0]):
                return t.name
        return self.terminals[-1].name

    def all_terminals(self) -> List[str]:
        return self.name_map.keys()

    def __getitem__(self, name: str):
        return self.name_map[name]

terminals = Terminals()
# TODO: option to switch between telnet and netcat
connect_command = "telnet localhost %port"
terminals.add_terminal(2, "tmux", f"tmux new-window -n \"{{name}} - %title\" \"{connect_command}\"")
terminals.add_terminal(2, "gnome-terminal", f"gnome-terminal --window --title \"{{name}} - %title\" --command \"{connect_command}\"")
terminals.add_terminal(1, "xterm", f"xterm -title \"{{name}} - %title\" -e {connect_command}")
terminals.add_terminal(0, "none", None)
