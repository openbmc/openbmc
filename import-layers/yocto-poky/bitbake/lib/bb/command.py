"""
BitBake 'Command' module

Provide an interface to interact with the bitbake server through 'commands'
"""

# Copyright (C) 2006-2007  Richard Purdie
#
# This program is free software; you can redistribute it and/or modify
# it under the terms of the GNU General Public License version 2 as
# published by the Free Software Foundation.
#
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.
#
# You should have received a copy of the GNU General Public License along
# with this program; if not, write to the Free Software Foundation, Inc.,
# 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.

"""
The bitbake server takes 'commands' from its UI/commandline.
Commands are either synchronous or asynchronous.
Async commands return data to the client in the form of events.
Sync commands must only return data through the function return value
and must not trigger events, directly or indirectly.
Commands are queued in a CommandQueue
"""

import bb.event
import bb.cooker

class CommandCompleted(bb.event.Event):
    pass

class CommandExit(bb.event.Event):
    def  __init__(self, exitcode):
        bb.event.Event.__init__(self)
        self.exitcode = int(exitcode)

class CommandFailed(CommandExit):
    def __init__(self, message):
        self.error = message
        CommandExit.__init__(self, 1)

class CommandError(Exception):
    pass

class Command:
    """
    A queue of asynchronous commands for bitbake
    """
    def __init__(self, cooker):
        self.cooker = cooker
        self.cmds_sync = CommandsSync()
        self.cmds_async = CommandsAsync()

        # FIXME Add lock for this
        self.currentAsyncCommand = None

    def runCommand(self, commandline, ro_only = False):
        command = commandline.pop(0)
        if hasattr(CommandsSync, command):
            # Can run synchronous commands straight away
            command_method = getattr(self.cmds_sync, command)
            if ro_only:
                if not hasattr(command_method, 'readonly') or False == getattr(command_method, 'readonly'):
                    return None, "Not able to execute not readonly commands in readonly mode"
            try:
                if getattr(command_method, 'needconfig', False):
                    self.cooker.updateCacheSync()
                result = command_method(self, commandline)
            except CommandError as exc:
                return None, exc.args[0]
            except (Exception, SystemExit):
                import traceback
                return None, traceback.format_exc()
            else:
                return result, None
        if self.currentAsyncCommand is not None:
            return None, "Busy (%s in progress)" % self.currentAsyncCommand[0]
        if command not in CommandsAsync.__dict__:
            return None, "No such command"
        self.currentAsyncCommand = (command, commandline)
        self.cooker.configuration.server_register_idlecallback(self.cooker.runCommands, self.cooker)
        return True, None

    def runAsyncCommand(self):
        try:
            if self.cooker.state in (bb.cooker.state.error, bb.cooker.state.shutdown, bb.cooker.state.forceshutdown):
                # updateCache will trigger a shutdown of the parser
                # and then raise BBHandledException triggering an exit
                self.cooker.updateCache()
                return False
            if self.currentAsyncCommand is not None:
                (command, options) = self.currentAsyncCommand
                commandmethod = getattr(CommandsAsync, command)
                needcache = getattr( commandmethod, "needcache" )
                if needcache and self.cooker.state != bb.cooker.state.running:
                    self.cooker.updateCache()
                    return True
                else:
                    commandmethod(self.cmds_async, self, options)
                    return False
            else:
                return False
        except KeyboardInterrupt as exc:
            self.finishAsyncCommand("Interrupted")
            return False
        except SystemExit as exc:
            arg = exc.args[0]
            if isinstance(arg, basestring):
                self.finishAsyncCommand(arg)
            else:
                self.finishAsyncCommand("Exited with %s" % arg)
            return False
        except Exception as exc:
            import traceback
            if isinstance(exc, bb.BBHandledException):
                self.finishAsyncCommand("")
            else:
                self.finishAsyncCommand(traceback.format_exc())
            return False

    def finishAsyncCommand(self, msg=None, code=None):
        if msg or msg == "":
            bb.event.fire(CommandFailed(msg), self.cooker.expanded_data)
        elif code:
            bb.event.fire(CommandExit(code), self.cooker.expanded_data)
        else:
            bb.event.fire(CommandCompleted(), self.cooker.expanded_data)
        self.currentAsyncCommand = None
        self.cooker.finishcommand()

class CommandsSync:
    """
    A class of synchronous commands
    These should run quickly so as not to hurt interactive performance.
    These must not influence any running synchronous command.
    """

    def stateShutdown(self, command, params):
        """
        Trigger cooker 'shutdown' mode
        """
        command.cooker.shutdown(False)

    def stateForceShutdown(self, command, params):
        """
        Stop the cooker
        """
        command.cooker.shutdown(True)

    def getAllKeysWithFlags(self, command, params):
        """
        Returns a dump of the global state. Call with
        variable flags to be retrieved as params.
        """
        flaglist = params[0]
        return command.cooker.getAllKeysWithFlags(flaglist)
    getAllKeysWithFlags.readonly = True

    def getVariable(self, command, params):
        """
        Read the value of a variable from data
        """
        varname = params[0]
        expand = True
        if len(params) > 1:
            expand = (params[1] == "True")

        return command.cooker.data.getVar(varname, expand)
    getVariable.readonly = True

    def setVariable(self, command, params):
        """
        Set the value of variable in data
        """
        varname = params[0]
        value = str(params[1])
        command.cooker.data.setVar(varname, value)

    def getSetVariable(self, command, params):
        """
        Read the value of a variable from data and set it into the datastore
        which effectively expands and locks the value.
        """
        varname = params[0]
        result = self.getVariable(command, params)
        command.cooker.data.setVar(varname, result)
        return result

    def setConfig(self, command, params):
        """
        Set the value of variable in configuration
        """
        varname = params[0]
        value = str(params[1])
        setattr(command.cooker.configuration, varname, value)

    def enableDataTracking(self, command, params):
        """
        Enable history tracking for variables
        """
        command.cooker.enableDataTracking()

    def disableDataTracking(self, command, params):
        """
        Disable history tracking for variables
        """
        command.cooker.disableDataTracking()

    def setPrePostConfFiles(self, command, params):
        prefiles = params[0].split()
        postfiles = params[1].split()
        command.cooker.configuration.prefile = prefiles
        command.cooker.configuration.postfile = postfiles
    setPrePostConfFiles.needconfig = False

    def getCpuCount(self, command, params):
        """
        Get the CPU count on the bitbake server
        """
        return bb.utils.cpu_count()
    getCpuCount.readonly = True
    getCpuCount.needconfig = False

    def matchFile(self, command, params):
        fMatch = params[0]
        return command.cooker.matchFile(fMatch)
    matchFile.needconfig = False

    def generateNewImage(self, command, params):
        image = params[0]
        base_image = params[1]
        package_queue = params[2]
        timestamp = params[3]
        description = params[4]
        return command.cooker.generateNewImage(image, base_image,
                        package_queue, timestamp, description)

    def ensureDir(self, command, params):
        directory = params[0]
        bb.utils.mkdirhier(directory)
    ensureDir.needconfig = False

    def setVarFile(self, command, params):
        """
        Save a variable in a file; used for saving in a configuration file
        """
        var = params[0]
        val = params[1]
        default_file = params[2]
        op = params[3]
        command.cooker.modifyConfigurationVar(var, val, default_file, op)
    setVarFile.needconfig = False

    def removeVarFile(self, command, params):
        """
        Remove a variable declaration from a file
        """
        var = params[0]
        command.cooker.removeConfigurationVar(var)
    removeVarFile.needconfig = False

    def createConfigFile(self, command, params):
        """
        Create an extra configuration file
        """
        name = params[0]
        command.cooker.createConfigFile(name)
    createConfigFile.needconfig = False

    def setEventMask(self, command, params):
        handlerNum = params[0]
        llevel = params[1]
        debug_domains = params[2]
        mask = params[3]
        return bb.event.set_UIHmask(handlerNum, llevel, debug_domains, mask)
    setEventMask.needconfig = False
    setEventMask.readonly = True

    def setFeatures(self, command, params):
        """
        Set the cooker features to include the passed list of features
        """
        features = params[0]
        command.cooker.setFeatures(features)
    setFeatures.needconfig = False
    # although we change the internal state of the cooker, this is transparent since
    # we always take and leave the cooker in state.initial
    setFeatures.readonly = True

    def updateConfig(self, command, params):
        options = params[0]
        environment = params[1]
        command.cooker.updateConfigOpts(options, environment)
    updateConfig.needconfig = False

class CommandsAsync:
    """
    A class of asynchronous commands
    These functions communicate via generated events.
    Any function that requires metadata parsing should be here.
    """

    def buildFile(self, command, params):
        """
        Build a single specified .bb file
        """
        bfile = params[0]
        task = params[1]

        command.cooker.buildFile(bfile, task)
    buildFile.needcache = False

    def buildTargets(self, command, params):
        """
        Build a set of targets
        """
        pkgs_to_build = params[0]
        task = params[1]

        command.cooker.buildTargets(pkgs_to_build, task)
    buildTargets.needcache = True

    def generateDepTreeEvent(self, command, params):
        """
        Generate an event containing the dependency information
        """
        pkgs_to_build = params[0]
        task = params[1]

        command.cooker.generateDepTreeEvent(pkgs_to_build, task)
        command.finishAsyncCommand()
    generateDepTreeEvent.needcache = True

    def generateDotGraph(self, command, params):
        """
        Dump dependency information to disk as .dot files
        """
        pkgs_to_build = params[0]
        task = params[1]

        command.cooker.generateDotGraphFiles(pkgs_to_build, task)
        command.finishAsyncCommand()
    generateDotGraph.needcache = True

    def generateTargetsTree(self, command, params):
        """
        Generate a tree of buildable targets.
        If klass is provided ensure all recipes that inherit the class are
        included in the package list.
        If pkg_list provided use that list (plus any extras brought in by
        klass) rather than generating a tree for all packages.
        """
        klass = params[0]
        pkg_list = params[1]

        command.cooker.generateTargetsTree(klass, pkg_list)
        command.finishAsyncCommand()
    generateTargetsTree.needcache = True

    def findCoreBaseFiles(self, command, params):
        """
        Find certain files in COREBASE directory. i.e. Layers
        """
        subdir = params[0]
        filename = params[1]

        command.cooker.findCoreBaseFiles(subdir, filename)
        command.finishAsyncCommand()
    findCoreBaseFiles.needcache = False

    def findConfigFiles(self, command, params):
        """
        Find config files which provide appropriate values
        for the passed configuration variable. i.e. MACHINE
        """
        varname = params[0]

        command.cooker.findConfigFiles(varname)
        command.finishAsyncCommand()
    findConfigFiles.needcache = False

    def findFilesMatchingInDir(self, command, params):
        """
        Find implementation files matching the specified pattern
        in the requested subdirectory of a BBPATH
        """
        pattern = params[0]
        directory = params[1]

        command.cooker.findFilesMatchingInDir(pattern, directory)
        command.finishAsyncCommand()
    findFilesMatchingInDir.needcache = False

    def findConfigFilePath(self, command, params):
        """
        Find the path of the requested configuration file
        """
        configfile = params[0]

        command.cooker.findConfigFilePath(configfile)
        command.finishAsyncCommand()
    findConfigFilePath.needcache = False

    def showVersions(self, command, params):
        """
        Show the currently selected versions
        """
        command.cooker.showVersions()
        command.finishAsyncCommand()
    showVersions.needcache = True

    def showEnvironmentTarget(self, command, params):
        """
        Print the environment of a target recipe
        (needs the cache to work out which recipe to use)
        """
        pkg = params[0]

        command.cooker.showEnvironment(None, pkg)
        command.finishAsyncCommand()
    showEnvironmentTarget.needcache = True

    def showEnvironment(self, command, params):
        """
        Print the standard environment
        or if specified the environment for a specified recipe
        """
        bfile = params[0]

        command.cooker.showEnvironment(bfile)
        command.finishAsyncCommand()
    showEnvironment.needcache = False

    def parseFiles(self, command, params):
        """
        Parse the .bb files
        """
        command.cooker.updateCache()
        command.finishAsyncCommand()
    parseFiles.needcache = True

    def compareRevisions(self, command, params):
        """
        Parse the .bb files
        """
        if bb.fetch.fetcher_compare_revisions(command.cooker.data):
            command.finishAsyncCommand(code=1)
        else:
            command.finishAsyncCommand()
    compareRevisions.needcache = True

    def triggerEvent(self, command, params):
        """
        Trigger a certain event
        """
        event = params[0]
        bb.event.fire(eval(event), command.cooker.data)
        command.currentAsyncCommand = None
    triggerEvent.needcache = False

    def resetCooker(self, command, params):
        """
        Reset the cooker to its initial state, thus forcing a reparse for
        any async command that has the needcache property set to True
        """
        command.cooker.reset()
        command.finishAsyncCommand()
    resetCooker.needcache = False

