#!/usr/bin/env python3
#
# SPDX-License-Identifier: GPL-2.0-only
#
# This file re-uses code spread throughout other Bitbake source files.
# As such, all other copyrights belong to their own right holders.
#


import os
import sys
import json
import pickle
import codecs


class EventPlayer:
    """Emulate a connection to a bitbake server."""

    def __init__(self, eventfile, variables):
        self.eventfile = eventfile
        self.variables = variables
        self.eventmask = []

    def waitEvent(self, _timeout):
        """Read event from the file."""
        line = self.eventfile.readline().strip()
        if not line:
            return
        try:
            decodedline = json.loads(line)
            if 'allvariables' in decodedline:
                self.variables = decodedline['allvariables']
                return
            if not 'vars' in decodedline:
                raise ValueError
            event_str = decodedline['vars'].encode('utf-8')
            event = pickle.loads(codecs.decode(event_str, 'base64'))
            event_name = "%s.%s" % (event.__module__, event.__class__.__name__)
            if event_name not in self.eventmask:
                return
            return event
        except ValueError as err:
            print("Failed loading ", line)
            raise err

    def runCommand(self, command_line):
        """Emulate running a command on the server."""
        name = command_line[0]

        if name == "getVariable":
            var_name = command_line[1]
            variable = self.variables.get(var_name)
            if variable:
                return variable['v'], None
            return None, "Missing variable %s" % var_name

        elif name == "getAllKeysWithFlags":
            dump = {}
            flaglist = command_line[1]
            for key, val in self.variables.items():
                try:
                    if not key.startswith("__"):
                        dump[key] = {
                            'v': val['v'],
                            'history' : val['history'],
                        }
                        for flag in flaglist:
                            dump[key][flag] = val[flag]
                except Exception as err:
                    print(err)
            return (dump, None)

        elif name == 'setEventMask':
            self.eventmask = command_line[-1]
            return True, None

        else:
            raise Exception("Command %s not implemented" % command_line[0])

    def getEventHandle(self):
        """
        This method is called by toasterui.
        The return value is passed to self.runCommand but not used there.
        """
        pass
