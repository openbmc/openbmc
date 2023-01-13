#
# BitBake Tests for the Event implementation (event.py)
#
# Copyright (C) 2017 Intel Corporation
#
# SPDX-License-Identifier: GPL-2.0-only
#

import collections
import importlib
import logging
import pickle
import threading
import time
import unittest
from unittest.mock import Mock
from unittest.mock import call

import bb
import bb.event
from bb.msg import BBLogFormatter


class EventQueueStubBase(object):
    """ Base class for EventQueueStub classes """
    def __init__(self):
        self.event_calls = []
        return

    def _store_event_data_string(self, event):
        if isinstance(event, logging.LogRecord):
            formatter = BBLogFormatter("%(levelname)s: %(message)s")
            self.event_calls.append(formatter.format(event))
        else:
            self.event_calls.append(bb.event.getName(event))
        return


class EventQueueStub(EventQueueStubBase):
    """ Class used as specification for UI event handler queue stub objects """
    def __init__(self):
        super(EventQueueStub, self).__init__()

    def send(self, event):
        super(EventQueueStub, self)._store_event_data_string(event)


class PickleEventQueueStub(EventQueueStubBase):
    """ Class used as specification for UI event handler queue stub objects
        with sendpickle method """
    def __init__(self):
        super(PickleEventQueueStub, self).__init__()

    def sendpickle(self, pickled_event):
        event = pickle.loads(pickled_event)
        super(PickleEventQueueStub, self)._store_event_data_string(event)


class UIClientStub(object):
    """ Class used as specification for UI event handler stub objects """
    def __init__(self):
        self.event = None


class EventHandlingTest(unittest.TestCase):
    """ Event handling test class """


    def setUp(self):
        self._test_process = Mock()
        ui_client1 = UIClientStub()
        ui_client2 = UIClientStub()
        self._test_ui1 = Mock(wraps=ui_client1)
        self._test_ui2 = Mock(wraps=ui_client2)
        importlib.reload(bb.event)

    def _create_test_handlers(self):
        """ Method used to create a test handler ordered dictionary """
        test_handlers = collections.OrderedDict()
        test_handlers["handler1"] = self._test_process.handler1
        test_handlers["handler2"] = self._test_process.handler2
        return test_handlers

    def test_class_handlers(self):
        """ Test set_class_handlers and get_class_handlers methods """
        test_handlers = self._create_test_handlers()
        bb.event.set_class_handlers(test_handlers)
        self.assertEqual(test_handlers,
                         bb.event.get_class_handlers())

    def test_handlers(self):
        """ Test set_handlers and get_handlers """
        test_handlers = self._create_test_handlers()
        bb.event.set_handlers(test_handlers)
        self.assertEqual(test_handlers,
                         bb.event.get_handlers())

    def test_clean_class_handlers(self):
        """ Test clean_class_handlers method """
        cleanDict = collections.OrderedDict()
        self.assertEqual(cleanDict,
                         bb.event.clean_class_handlers())

    def test_register(self):
        """ Test register method for class handlers """
        result = bb.event.register("handler", self._test_process.handler)
        self.assertEqual(result, bb.event.Registered)
        handlers_dict = bb.event.get_class_handlers()
        self.assertIn("handler", handlers_dict)

    def test_already_registered(self):
        """ Test detection of an already registed class handler """
        bb.event.register("handler", self._test_process.handler)
        handlers_dict = bb.event.get_class_handlers()
        self.assertIn("handler", handlers_dict)
        result = bb.event.register("handler", self._test_process.handler)
        self.assertEqual(result, bb.event.AlreadyRegistered)

    def test_register_from_string(self):
        """ Test register method receiving code in string """
        result = bb.event.register("string_handler", "    return True")
        self.assertEqual(result, bb.event.Registered)
        handlers_dict = bb.event.get_class_handlers()
        self.assertIn("string_handler", handlers_dict)

    def test_register_with_mask(self):
        """ Test register method with event masking """
        mask = ["bb.event.OperationStarted",
                "bb.event.OperationCompleted"]
        result = bb.event.register("event_handler",
                                   self._test_process.event_handler,
                                   mask)
        self.assertEqual(result, bb.event.Registered)
        handlers_dict = bb.event.get_class_handlers()
        self.assertIn("event_handler", handlers_dict)

    def test_remove(self):
        """ Test remove method for class handlers """
        test_handlers = self._create_test_handlers()
        bb.event.set_class_handlers(test_handlers)
        count = len(test_handlers)
        bb.event.remove("handler1", None)
        test_handlers = bb.event.get_class_handlers()
        self.assertEqual(len(test_handlers), count - 1)
        with self.assertRaises(KeyError):
            bb.event.remove("handler1", None)

    def test_execute_handler(self):
        """ Test execute_handler method for class handlers """
        mask = ["bb.event.OperationProgress"]
        result = bb.event.register("event_handler",
                                   self._test_process.event_handler,
                                   mask)
        self.assertEqual(result, bb.event.Registered)
        event = bb.event.OperationProgress(current=10, total=100)
        bb.event.execute_handler("event_handler",
                                 self._test_process.event_handler,
                                 event,
                                 None)
        self._test_process.event_handler.assert_called_once_with(event, None)

    def test_fire_class_handlers(self):
        """ Test fire_class_handlers method """
        mask = ["bb.event.OperationStarted"]
        result = bb.event.register("event_handler1",
                                   self._test_process.event_handler1,
                                   mask)
        self.assertEqual(result, bb.event.Registered)
        result = bb.event.register("event_handler2",
                                   self._test_process.event_handler2,
                                   "*")
        self.assertEqual(result, bb.event.Registered)
        event1 = bb.event.OperationStarted()
        event2 = bb.event.OperationCompleted(total=123)
        bb.event.fire_class_handlers(event1, None)
        bb.event.fire_class_handlers(event2, None)
        bb.event.fire_class_handlers(event2, None)
        expected_event_handler1 = [call(event1, None)]
        expected_event_handler2 = [call(event1, None),
                                   call(event2, None),
                                   call(event2, None)]
        self.assertEqual(self._test_process.event_handler1.call_args_list,
                         expected_event_handler1)
        self.assertEqual(self._test_process.event_handler2.call_args_list,
                         expected_event_handler2)

    def test_class_handler_filters(self):
        """ Test filters for class handlers """
        mask = ["bb.event.OperationStarted"]
        result = bb.event.register("event_handler1",
                                   self._test_process.event_handler1,
                                   mask)
        self.assertEqual(result, bb.event.Registered)
        result = bb.event.register("event_handler2",
                                   self._test_process.event_handler2,
                                   "*")
        self.assertEqual(result, bb.event.Registered)
        bb.event.set_eventfilter(
            lambda name, handler, event, d :
            name == 'event_handler2' and
            bb.event.getName(event) == "OperationStarted")
        event1 = bb.event.OperationStarted()
        event2 = bb.event.OperationCompleted(total=123)
        bb.event.fire_class_handlers(event1, None)
        bb.event.fire_class_handlers(event2, None)
        bb.event.fire_class_handlers(event2, None)
        expected_event_handler1 = []
        expected_event_handler2 = [call(event1, None)]
        self.assertEqual(self._test_process.event_handler1.call_args_list,
                         expected_event_handler1)
        self.assertEqual(self._test_process.event_handler2.call_args_list,
                         expected_event_handler2)

    def test_change_handler_event_mapping(self):
        """ Test changing the event mapping for class handlers """
        event1 = bb.event.OperationStarted()
        event2 = bb.event.OperationCompleted(total=123)

        # register handler for all events
        result = bb.event.register("event_handler1",
                                   self._test_process.event_handler1,
                                   "*")
        self.assertEqual(result, bb.event.Registered)
        bb.event.fire_class_handlers(event1, None)
        bb.event.fire_class_handlers(event2, None)
        expected = [call(event1, None), call(event2, None)]
        self.assertEqual(self._test_process.event_handler1.call_args_list,
                         expected)

        # unregister handler and register it only for OperationStarted
        bb.event.remove("event_handler1",
                        self._test_process.event_handler1)
        mask = ["bb.event.OperationStarted"]
        result = bb.event.register("event_handler1",
                                   self._test_process.event_handler1,
                                   mask)
        self.assertEqual(result, bb.event.Registered)
        bb.event.fire_class_handlers(event1, None)
        bb.event.fire_class_handlers(event2, None)
        expected = [call(event1, None), call(event2, None), call(event1, None)]
        self.assertEqual(self._test_process.event_handler1.call_args_list,
                         expected)

        # unregister handler and register it only for OperationCompleted
        bb.event.remove("event_handler1",
                        self._test_process.event_handler1)
        mask = ["bb.event.OperationCompleted"]
        result = bb.event.register("event_handler1",
                                   self._test_process.event_handler1,
                                   mask)
        self.assertEqual(result, bb.event.Registered)
        bb.event.fire_class_handlers(event1, None)
        bb.event.fire_class_handlers(event2, None)
        expected = [call(event1,None), call(event2, None), call(event1, None), call(event2, None)]
        self.assertEqual(self._test_process.event_handler1.call_args_list,
                         expected)

    def test_register_UIHhandler(self):
        """ Test register_UIHhandler method """
        result = bb.event.register_UIHhandler(self._test_ui1, mainui=True)
        self.assertEqual(result, 1)

    def test_UIHhandler_already_registered(self):
        """ Test registering an UIHhandler already existing """
        result = bb.event.register_UIHhandler(self._test_ui1, mainui=True)
        self.assertEqual(result, 1)
        result = bb.event.register_UIHhandler(self._test_ui1, mainui=True)
        self.assertEqual(result, 2)

    def test_unregister_UIHhandler(self):
        """ Test unregister_UIHhandler method """
        result = bb.event.register_UIHhandler(self._test_ui1, mainui=True)
        self.assertEqual(result, 1)
        result = bb.event.unregister_UIHhandler(1)
        self.assertIs(result, None)

    def test_fire_ui_handlers(self):
        """ Test fire_ui_handlers method """
        self._test_ui1.event = Mock(spec_set=EventQueueStub)
        result = bb.event.register_UIHhandler(self._test_ui1, mainui=True)
        self.assertEqual(result, 1)
        self._test_ui2.event = Mock(spec_set=PickleEventQueueStub)
        result = bb.event.register_UIHhandler(self._test_ui2, mainui=True)
        self.assertEqual(result, 2)
        event1 = bb.event.OperationStarted()
        bb.event.fire_ui_handlers(event1, None)
        expected = [call(event1)]
        self.assertEqual(self._test_ui1.event.send.call_args_list,
                         expected)
        expected = [call(pickle.dumps(event1))]
        self.assertEqual(self._test_ui2.event.sendpickle.call_args_list,
                         expected)

    def test_ui_handler_mask_filter(self):
        """ Test filters for UI handlers """
        mask = ["bb.event.OperationStarted"]
        debug_domains = {}
        self._test_ui1.event = Mock(spec_set=EventQueueStub)
        result = bb.event.register_UIHhandler(self._test_ui1, mainui=True)
        bb.event.set_UIHmask(result, logging.INFO, debug_domains, mask)
        self._test_ui2.event = Mock(spec_set=PickleEventQueueStub)
        result = bb.event.register_UIHhandler(self._test_ui2, mainui=True)
        bb.event.set_UIHmask(result, logging.INFO, debug_domains, mask)

        event1 = bb.event.OperationStarted()
        event2 = bb.event.OperationCompleted(total=1)

        bb.event.fire_ui_handlers(event1, None)
        bb.event.fire_ui_handlers(event2, None)
        expected = [call(event1)]
        self.assertEqual(self._test_ui1.event.send.call_args_list,
                         expected)
        expected = [call(pickle.dumps(event1))]
        self.assertEqual(self._test_ui2.event.sendpickle.call_args_list,
                         expected)

    def test_ui_handler_log_filter(self):
        """ Test log filters for UI handlers """
        mask = ["*"]
        debug_domains = {'BitBake.Foo': logging.WARNING}

        self._test_ui1.event = EventQueueStub()
        result = bb.event.register_UIHhandler(self._test_ui1, mainui=True)
        bb.event.set_UIHmask(result, logging.ERROR, debug_domains, mask)
        self._test_ui2.event = PickleEventQueueStub()
        result = bb.event.register_UIHhandler(self._test_ui2, mainui=True)
        bb.event.set_UIHmask(result, logging.ERROR, debug_domains, mask)

        event1 = bb.event.OperationStarted()
        bb.event.fire_ui_handlers(event1, None)   # All events match

        event_log_handler = bb.event.LogHandler()
        logger = logging.getLogger("BitBake")
        logger.addHandler(event_log_handler)
        logger1 = logging.getLogger("BitBake.Foo")
        logger1.warning("Test warning LogRecord1") # Matches debug_domains level
        logger1.info("Test info LogRecord")        # Filtered out
        logger2 = logging.getLogger("BitBake.Bar")
        logger2.error("Test error LogRecord")      # Matches filter base level
        logger2.warning("Test warning LogRecord2") # Filtered out
        logger.removeHandler(event_log_handler)

        expected = ['OperationStarted',
                    'WARNING: Test warning LogRecord1',
                    'ERROR: Test error LogRecord']
        self.assertEqual(self._test_ui1.event.event_calls, expected)
        self.assertEqual(self._test_ui2.event.event_calls, expected)

    def test_fire(self):
        """ Test fire method used to trigger class and ui event handlers """
        mask = ["bb.event.ConfigParsed"]
        result = bb.event.register("event_handler1",
                                   self._test_process.event_handler1,
                                   mask)

        self._test_ui1.event = Mock(spec_set=EventQueueStub)
        result = bb.event.register_UIHhandler(self._test_ui1, mainui=True)
        self.assertEqual(result, 1)

        event1 = bb.event.ConfigParsed()
        bb.event.fire(event1, None)
        expected = [call(event1, None)]
        self.assertEqual(self._test_process.event_handler1.call_args_list,
                         expected)
        expected = [call(event1)]
        self.assertEqual(self._test_ui1.event.send.call_args_list,
                         expected)

    def test_fire_from_worker(self):
        """ Test fire_from_worker method """
        self._test_ui1.event = Mock(spec_set=EventQueueStub)
        result = bb.event.register_UIHhandler(self._test_ui1, mainui=True)
        self.assertEqual(result, 1)
        event1 = bb.event.ConfigParsed()
        bb.event.fire_from_worker(event1, None)
        expected = [call(event1)]
        self.assertEqual(self._test_ui1.event.send.call_args_list,
                         expected)

    def test_worker_fire(self):
        """ Test the triggering of bb.event.worker_fire callback """
        bb.event.worker_fire = Mock()
        event = bb.event.Event()
        bb.event.fire(event, None)
        expected = [call(event, None)]
        self.assertEqual(bb.event.worker_fire.call_args_list, expected)

    def test_print_ui_queue(self):
        """ Test print_ui_queue method """
        event1 = bb.event.OperationStarted()
        event2 = bb.event.OperationCompleted(total=123)
        bb.event.fire(event1, None)
        bb.event.fire(event2, None)
        event_log_handler = bb.event.LogHandler()
        logger = logging.getLogger("BitBake")
        logger.addHandler(event_log_handler)
        logger.info("Test info LogRecord")
        logger.warning("Test warning LogRecord")
        with self.assertLogs("BitBake", level="INFO") as cm:
            bb.event.print_ui_queue()
        logger.removeHandler(event_log_handler)
        self.assertEqual(cm.output,
                         ["INFO:BitBake:Test info LogRecord",
                          "WARNING:BitBake:Test warning LogRecord"])

    def _set_threadlock_test_mockups(self):
        """ Create UI event handler mockups used in enable and disable
            threadlock tests """
        def ui1_event_send(event):
            if type(event) is bb.event.ConfigParsed:
                self._threadlock_test_calls.append("w1_ui1")
            if type(event) is bb.event.OperationStarted:
                self._threadlock_test_calls.append("w2_ui1")
            time.sleep(2)

        def ui2_event_send(event):
            if type(event) is bb.event.ConfigParsed:
                self._threadlock_test_calls.append("w1_ui2")
            if type(event) is bb.event.OperationStarted:
                self._threadlock_test_calls.append("w2_ui2")
            time.sleep(2)

        self._threadlock_test_calls = []
        self._test_ui1.event = EventQueueStub()
        self._test_ui1.event.send = ui1_event_send
        result = bb.event.register_UIHhandler(self._test_ui1, mainui=True)
        self.assertEqual(result, 1)
        self._test_ui2.event = EventQueueStub()
        self._test_ui2.event.send = ui2_event_send
        result = bb.event.register_UIHhandler(self._test_ui2, mainui=True)
        self.assertEqual(result, 2)

    def _set_and_run_threadlock_test_workers(self):
        """ Create and run the workers used to trigger events in enable and
            disable threadlock tests """
        worker1 = threading.Thread(target=self._thread_lock_test_worker1)
        worker2 = threading.Thread(target=self._thread_lock_test_worker2)
        worker1.start()
        time.sleep(1)
        worker2.start()
        worker1.join()
        worker2.join()

    def _thread_lock_test_worker1(self):
        """ First worker used to fire the ConfigParsed event for enable and
            disable threadlocks tests """
        bb.event.fire(bb.event.ConfigParsed(), None)

    def _thread_lock_test_worker2(self):
        """ Second worker used to fire the OperationStarted event for enable
            and disable threadlocks tests """
        bb.event.fire(bb.event.OperationStarted(), None)

    def test_event_threadlock(self):
        """ Test enable_threadlock method """
        self._set_threadlock_test_mockups()
        self._set_and_run_threadlock_test_workers()
        # Calls to UI handlers should be in order as all the registered
        # handlers for the event coming from the first worker should be
        # called before processing the event from the second worker.
        self.assertEqual(self._threadlock_test_calls,
                         ["w1_ui1", "w1_ui2", "w2_ui1", "w2_ui2"])

class EventClassesTest(unittest.TestCase):
    """ Event classes test class """

    _worker_pid = 54321

    def setUp(self):
        bb.event.worker_pid = EventClassesTest._worker_pid

    def test_Event(self):
        """ Test the Event base class """
        event = bb.event.Event()
        self.assertEqual(event.pid, EventClassesTest._worker_pid)

    def test_HeartbeatEvent(self):
        """ Test the HeartbeatEvent class """
        time = 10
        event = bb.event.HeartbeatEvent(time)
        self.assertEqual(event.time, time)
        self.assertEqual(event.pid, EventClassesTest._worker_pid)

    def test_OperationStarted(self):
        """ Test OperationStarted event class """
        msg = "Foo Bar"
        event = bb.event.OperationStarted(msg)
        self.assertEqual(event.msg, msg)
        self.assertEqual(event.pid, EventClassesTest._worker_pid)

    def test_OperationCompleted(self):
        """ Test OperationCompleted event class """
        msg = "Foo Bar"
        total = 123
        event = bb.event.OperationCompleted(total, msg)
        self.assertEqual(event.msg, msg)
        self.assertEqual(event.total, total)
        self.assertEqual(event.pid, EventClassesTest._worker_pid)

    def test_OperationProgress(self):
        """ Test OperationProgress event class """
        msg = "Foo Bar"
        total = 123
        current = 111
        event = bb.event.OperationProgress(current, total, msg)
        self.assertEqual(event.msg, msg + ": %s/%s" % (current, total))
        self.assertEqual(event.pid, EventClassesTest._worker_pid)

    def test_ConfigParsed(self):
        """ Test the ConfigParsed class """
        event = bb.event.ConfigParsed()
        self.assertEqual(event.pid, EventClassesTest._worker_pid)

    def test_MultiConfigParsed(self):
        """ Test MultiConfigParsed event class """
        mcdata = {"foobar": "Foo Bar"}
        event = bb.event.MultiConfigParsed(mcdata)
        self.assertEqual(event.mcdata, mcdata)
        self.assertEqual(event.pid, EventClassesTest._worker_pid)

    def test_RecipeEvent(self):
        """ Test RecipeEvent event base class """
        callback = lambda a: 2 * a
        event = bb.event.RecipeEvent(callback)
        self.assertEqual(event.fn(1), callback(1))
        self.assertEqual(event.pid, EventClassesTest._worker_pid)

    def test_RecipePreFinalise(self):
        """ Test RecipePreFinalise event class """
        callback = lambda a: 2 * a
        event = bb.event.RecipePreFinalise(callback)
        self.assertEqual(event.fn(1), callback(1))
        self.assertEqual(event.pid, EventClassesTest._worker_pid)

    def test_RecipeTaskPreProcess(self):
        """ Test RecipeTaskPreProcess event class """
        callback = lambda a: 2 * a
        tasklist = [("foobar", callback)]
        event = bb.event.RecipeTaskPreProcess(callback, tasklist)
        self.assertEqual(event.fn(1), callback(1))
        self.assertEqual(event.tasklist, tasklist)
        self.assertEqual(event.pid, EventClassesTest._worker_pid)

    def test_RecipeParsed(self):
        """ Test RecipeParsed event base class """
        callback = lambda a: 2 * a
        event = bb.event.RecipeParsed(callback)
        self.assertEqual(event.fn(1), callback(1))
        self.assertEqual(event.pid, EventClassesTest._worker_pid)

    def test_BuildBase(self):
        """ Test base class for bitbake build events """
        name = "foo"
        pkgs = ["bar"]
        failures = 123
        event = bb.event.BuildBase(name, pkgs, failures)
        self.assertEqual(event.name, name)
        self.assertEqual(event.pkgs, pkgs)
        self.assertEqual(event.getFailures(), failures)
        name = event.name = "bar"
        pkgs = event.pkgs = ["foo"]
        self.assertEqual(event.name, name)
        self.assertEqual(event.pkgs, pkgs)
        self.assertEqual(event.getFailures(), failures)
        self.assertEqual(event.pid, EventClassesTest._worker_pid)

    def test_BuildInit(self):
        """ Test class for bitbake build invocation events """
        event = bb.event.BuildInit()
        self.assertEqual(event.name, None)
        self.assertEqual(event.pkgs, [])
        self.assertEqual(event.getFailures(), 0)
        name = event.name = "bar"
        pkgs = event.pkgs = ["foo"]
        self.assertEqual(event.name, name)
        self.assertEqual(event.pkgs, pkgs)
        self.assertEqual(event.getFailures(), 0)
        self.assertEqual(event.pid, EventClassesTest._worker_pid)

    def test_BuildStarted(self):
        """ Test class for build started events """
        name = "foo"
        pkgs = ["bar"]
        failures = 123
        event = bb.event.BuildStarted(name, pkgs, failures)
        self.assertEqual(event.name, name)
        self.assertEqual(event.pkgs, pkgs)
        self.assertEqual(event.getFailures(), failures)
        self.assertEqual(event.msg, "Building Started")
        name = event.name = "bar"
        pkgs = event.pkgs = ["foo"]
        msg = event.msg = "foobar"
        self.assertEqual(event.name, name)
        self.assertEqual(event.pkgs, pkgs)
        self.assertEqual(event.getFailures(), failures)
        self.assertEqual(event.msg, msg)
        self.assertEqual(event.pid, EventClassesTest._worker_pid)

    def test_BuildCompleted(self):
        """ Test class for build completed events """
        total = 1000
        name = "foo"
        pkgs = ["bar"]
        failures = 123
        interrupted = 1
        event = bb.event.BuildCompleted(total, name, pkgs, failures,
                                        interrupted)
        self.assertEqual(event.name, name)
        self.assertEqual(event.pkgs, pkgs)
        self.assertEqual(event.getFailures(), failures)
        self.assertEqual(event.msg, "Building Failed")
        event2 = bb.event.BuildCompleted(total, name, pkgs)
        self.assertEqual(event2.name, name)
        self.assertEqual(event2.pkgs, pkgs)
        self.assertEqual(event2.getFailures(), 0)
        self.assertEqual(event2.msg, "Building Succeeded")
        self.assertEqual(event2.pid, EventClassesTest._worker_pid)

    def test_DiskFull(self):
        """ Test DiskFull event class """
        dev = "/dev/foo"
        type = "ext4"
        freespace = "104M"
        mountpoint = "/"
        event = bb.event.DiskFull(dev, type, freespace, mountpoint)
        self.assertEqual(event.pid, EventClassesTest._worker_pid)

    def test_MonitorDiskEvent(self):
        """ Test MonitorDiskEvent class """
        available_bytes = 10000000
        free_bytes = 90000000
        total_bytes = 1000000000
        du = bb.event.DiskUsageSample(available_bytes, free_bytes,
                                      total_bytes)
        event = bb.event.MonitorDiskEvent(du)
        self.assertEqual(event.disk_usage.available_bytes, available_bytes)
        self.assertEqual(event.disk_usage.free_bytes, free_bytes)
        self.assertEqual(event.disk_usage.total_bytes, total_bytes)
        self.assertEqual(event.pid, EventClassesTest._worker_pid)

    def test_NoProvider(self):
        """ Test NoProvider event class """
        item = "foobar"
        event1 = bb.event.NoProvider(item)
        self.assertEqual(event1.getItem(), item)
        self.assertEqual(event1.isRuntime(), False)
        self.assertEqual(str(event1), "Nothing PROVIDES 'foobar'")
        runtime = True
        dependees = ["foo", "bar"]
        reasons = None
        close_matches = ["foibar", "footbar"]
        event2 = bb.event.NoProvider(item, runtime, dependees, reasons,
                                     close_matches)
        self.assertEqual(event2.isRuntime(), True)
        expected = ("Nothing RPROVIDES 'foobar' (but foo, bar RDEPENDS"
                    " on or otherwise requires it). Close matches:\n"
                    "  foibar\n"
                    "  footbar")
        self.assertEqual(str(event2), expected)
        reasons = ["Item does not exist on database"]
        close_matches = ["foibar", "footbar"]
        event3 = bb.event.NoProvider(item, runtime, dependees, reasons,
                                     close_matches)
        expected = ("Nothing RPROVIDES 'foobar' (but foo, bar RDEPENDS"
                    " on or otherwise requires it)\n"
                    "Item does not exist on database")
        self.assertEqual(str(event3), expected)
        self.assertEqual(event3.pid, EventClassesTest._worker_pid)

    def test_MultipleProviders(self):
        """ Test MultipleProviders event class """
        item = "foobar"
        candidates = ["foobarv1", "foobars"]
        event1 = bb.event.MultipleProviders(item, candidates)
        self.assertEqual(event1.isRuntime(), False)
        self.assertEqual(event1.getItem(), item)
        self.assertEqual(event1.getCandidates(), candidates)
        expected = ("Multiple providers are available for foobar (foobarv1,"
                    " foobars)\n"
                    "Consider defining a PREFERRED_PROVIDER entry to match "
                    "foobar")
        self.assertEqual(str(event1), expected)
        runtime = True
        event2 = bb.event.MultipleProviders(item, candidates, runtime)
        self.assertEqual(event2.isRuntime(), runtime)
        expected = ("Multiple providers are available for runtime foobar "
                    "(foobarv1, foobars)\n"
                    "Consider defining a PREFERRED_RPROVIDER entry to match "
                    "foobar")
        self.assertEqual(str(event2), expected)
        self.assertEqual(event2.pid, EventClassesTest._worker_pid)

    def test_ParseStarted(self):
        """ Test ParseStarted event class """
        total = 123
        event = bb.event.ParseStarted(total)
        self.assertEqual(event.msg, "Recipe parsing Started")
        self.assertEqual(event.total, total)
        self.assertEqual(event.pid, EventClassesTest._worker_pid)

    def test_ParseCompleted(self):
        """ Test ParseCompleted event class """
        cached = 10
        parsed = 13
        skipped = 7
        virtuals = 2
        masked = 1
        errors = 0
        total = 23
        event = bb.event.ParseCompleted(cached, parsed, skipped, masked,
                                        virtuals, errors, total)
        self.assertEqual(event.msg, "Recipe parsing Completed")
        expected = [cached, parsed, skipped, virtuals, masked, errors,
                    cached + parsed, total]
        actual = [event.cached, event.parsed, event.skipped, event.virtuals,
                  event.masked, event.errors, event.sofar, event.total]
        self.assertEqual(str(actual), str(expected))
        self.assertEqual(event.pid, EventClassesTest._worker_pid)

    def test_ParseProgress(self):
        """ Test ParseProgress event class """
        current = 10
        total = 100
        event = bb.event.ParseProgress(current, total)
        self.assertEqual(event.msg,
                         "Recipe parsing" + ": %s/%s" % (current, total))
        self.assertEqual(event.pid, EventClassesTest._worker_pid)

    def test_CacheLoadStarted(self):
        """ Test CacheLoadStarted event class """
        total = 123
        event = bb.event.CacheLoadStarted(total)
        self.assertEqual(event.msg, "Loading cache Started")
        self.assertEqual(event.total, total)
        self.assertEqual(event.pid, EventClassesTest._worker_pid)

    def test_CacheLoadProgress(self):
        """ Test CacheLoadProgress event class """
        current = 10
        total = 100
        event = bb.event.CacheLoadProgress(current, total)
        self.assertEqual(event.msg,
                         "Loading cache" + ": %s/%s" % (current, total))
        self.assertEqual(event.pid, EventClassesTest._worker_pid)

    def test_CacheLoadCompleted(self):
        """ Test CacheLoadCompleted event class """
        total = 23
        num_entries = 12
        event = bb.event.CacheLoadCompleted(total, num_entries)
        self.assertEqual(event.msg, "Loading cache Completed")
        expected = [total, num_entries]
        actual = [event.total, event.num_entries]
        self.assertEqual(str(actual), str(expected))
        self.assertEqual(event.pid, EventClassesTest._worker_pid)

    def test_TreeDataPreparationStarted(self):
        """ Test TreeDataPreparationStarted event class """
        event = bb.event.TreeDataPreparationStarted()
        self.assertEqual(event.msg, "Preparing tree data Started")
        self.assertEqual(event.pid, EventClassesTest._worker_pid)

    def test_TreeDataPreparationProgress(self):
        """ Test TreeDataPreparationProgress event class """
        current = 10
        total = 100
        event = bb.event.TreeDataPreparationProgress(current, total)
        self.assertEqual(event.msg,
                         "Preparing tree data" + ": %s/%s" % (current, total))
        self.assertEqual(event.pid, EventClassesTest._worker_pid)

    def test_TreeDataPreparationCompleted(self):
        """ Test TreeDataPreparationCompleted event class """
        total = 23
        event = bb.event.TreeDataPreparationCompleted(total)
        self.assertEqual(event.msg, "Preparing tree data Completed")
        self.assertEqual(event.total, total)
        self.assertEqual(event.pid, EventClassesTest._worker_pid)

    def test_DepTreeGenerated(self):
        """ Test DepTreeGenerated event class """
        depgraph = Mock()
        event = bb.event.DepTreeGenerated(depgraph)
        self.assertEqual(event.pid, EventClassesTest._worker_pid)

    def test_TargetsTreeGenerated(self):
        """ Test TargetsTreeGenerated event class """
        model = Mock()
        event = bb.event.TargetsTreeGenerated(model)
        self.assertEqual(event.pid, EventClassesTest._worker_pid)

    def test_ReachableStamps(self):
        """ Test ReachableStamps event class """
        stamps = [Mock(), Mock()]
        event = bb.event.ReachableStamps(stamps)
        self.assertEqual(event.stamps, stamps)
        self.assertEqual(event.pid, EventClassesTest._worker_pid)

    def test_FilesMatchingFound(self):
        """ Test FilesMatchingFound event class """
        pattern = "foo.*bar"
        matches = ["foobar"]
        event = bb.event.FilesMatchingFound(pattern, matches)
        self.assertEqual(event.pid, EventClassesTest._worker_pid)

    def test_ConfigFilesFound(self):
        """ Test ConfigFilesFound event class """
        variable = "FOO_BAR"
        values = ["foo", "bar"]
        event = bb.event.ConfigFilesFound(variable, values)
        self.assertEqual(event.pid, EventClassesTest._worker_pid)

    def test_ConfigFilePathFound(self):
        """ Test ConfigFilePathFound event class """
        path = "/foo/bar"
        event = bb.event.ConfigFilePathFound(path)
        self.assertEqual(event.pid, EventClassesTest._worker_pid)

    def test_message_classes(self):
        """ Test message event classes """
        msg = "foobar foo bar"
        event = bb.event.MsgBase(msg)
        self.assertEqual(event.pid, EventClassesTest._worker_pid)
        event = bb.event.MsgDebug(msg)
        self.assertEqual(event.pid, EventClassesTest._worker_pid)
        event = bb.event.MsgNote(msg)
        self.assertEqual(event.pid, EventClassesTest._worker_pid)
        event = bb.event.MsgWarn(msg)
        self.assertEqual(event.pid, EventClassesTest._worker_pid)
        event = bb.event.MsgError(msg)
        self.assertEqual(event.pid, EventClassesTest._worker_pid)
        event = bb.event.MsgFatal(msg)
        self.assertEqual(event.pid, EventClassesTest._worker_pid)
        event = bb.event.MsgPlain(msg)
        self.assertEqual(event.pid, EventClassesTest._worker_pid)

    def test_LogExecTTY(self):
        """ Test LogExecTTY event class """
        msg = "foo bar"
        prog = "foo.sh"
        sleep_delay = 10
        retries = 3
        event = bb.event.LogExecTTY(msg, prog, sleep_delay, retries)
        self.assertEqual(event.msg, msg)
        self.assertEqual(event.prog, prog)
        self.assertEqual(event.sleep_delay, sleep_delay)
        self.assertEqual(event.retries, retries)
        self.assertEqual(event.pid, EventClassesTest._worker_pid)

    def _throw_zero_division_exception(self):
        a = 1 / 0
        return

    def _worker_handler(self, event, d):
        self._returned_event = event
        return

    def test_LogHandler(self):
        """ Test LogHandler class """
        logger = logging.getLogger("TestEventClasses")
        logger.propagate = False
        handler = bb.event.LogHandler(logging.INFO)
        logger.addHandler(handler)
        bb.event.worker_fire = self._worker_handler
        try:
            self._throw_zero_division_exception()
        except ZeroDivisionError as ex:
            logger.exception(ex)
        event = self._returned_event
        try:
            pe = pickle.dumps(event)
            newevent = pickle.loads(pe)
        except:
            self.fail('Logged event is not serializable')
        self.assertEqual(event.taskpid, EventClassesTest._worker_pid)

    def test_MetadataEvent(self):
        """ Test MetadataEvent class """
        eventtype = "footype"
        eventdata = {"foo": "bar"}
        event = bb.event.MetadataEvent(eventtype, eventdata)
        self.assertEqual(event.type, eventtype)
        self.assertEqual(event.pid, EventClassesTest._worker_pid)

    def test_ProcessStarted(self):
        """ Test ProcessStarted class """
        processname = "foo"
        total = 9783128974
        event = bb.event.ProcessStarted(processname, total)
        self.assertEqual(event.processname, processname)
        self.assertEqual(event.total, total)
        self.assertEqual(event.pid, EventClassesTest._worker_pid)

    def test_ProcessProgress(self):
        """ Test ProcessProgress class """
        processname = "foo"
        progress = 243224
        event = bb.event.ProcessProgress(processname, progress)
        self.assertEqual(event.processname, processname)
        self.assertEqual(event.progress, progress)
        self.assertEqual(event.pid, EventClassesTest._worker_pid)

    def test_ProcessFinished(self):
        """ Test ProcessFinished class """
        processname = "foo"
        total = 1242342344
        event = bb.event.ProcessFinished(processname)
        self.assertEqual(event.processname, processname)
        self.assertEqual(event.pid, EventClassesTest._worker_pid)

    def test_SanityCheck(self):
        """ Test SanityCheck class """
        event1 = bb.event.SanityCheck()
        self.assertEqual(event1.generateevents, True)
        self.assertEqual(event1.pid, EventClassesTest._worker_pid)
        generateevents = False
        event2 = bb.event.SanityCheck(generateevents)
        self.assertEqual(event2.generateevents, generateevents)
        self.assertEqual(event2.pid, EventClassesTest._worker_pid)

    def test_SanityCheckPassed(self):
        """ Test SanityCheckPassed class """
        event = bb.event.SanityCheckPassed()
        self.assertEqual(event.pid, EventClassesTest._worker_pid)

    def test_SanityCheckFailed(self):
        """ Test SanityCheckFailed class """
        msg = "The sanity test failed."
        event1 = bb.event.SanityCheckFailed(msg)
        self.assertEqual(event1.pid, EventClassesTest._worker_pid)
        network_error = True
        event2 = bb.event.SanityCheckFailed(msg, network_error)
        self.assertEqual(event2.pid, EventClassesTest._worker_pid)

    def test_network_event_classes(self):
        """ Test network event classes """
        event1 = bb.event.NetworkTest()
        generateevents = False
        self.assertEqual(event1.pid, EventClassesTest._worker_pid)
        event2 = bb.event.NetworkTest(generateevents)
        self.assertEqual(event2.pid, EventClassesTest._worker_pid)
        event3 = bb.event.NetworkTestPassed()
        self.assertEqual(event3.pid, EventClassesTest._worker_pid)
        event4 = bb.event.NetworkTestFailed()
        self.assertEqual(event4.pid, EventClassesTest._worker_pid)

    def test_FindSigInfoResult(self):
        """ Test FindSigInfoResult event class """
        result = [Mock()]
        event = bb.event.FindSigInfoResult(result)
        self.assertEqual(event.result, result)
        self.assertEqual(event.pid, EventClassesTest._worker_pid)
