# ex:ts=4:sw=4:sts=4:et
# -*- tab-width: 4; c-basic-offset: 4; indent-tabs-mode: nil -*-
#
# BitBake Tests for the Event implementation (event.py)
#
# Copyright (C) 2017 Intel Corporation
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
#

import unittest
import bb
import logging
import bb.compat
import bb.event
import importlib
import threading
import time
import pickle
from unittest.mock import Mock
from unittest.mock import call


class EventQueueStub():
    """ Class used as specification for UI event handler queue stub objects """
    def __init__(self):
        return

    def send(self, event):
        return


class PickleEventQueueStub():
    """ Class used as specification for UI event handler queue stub objects
        with sendpickle method """
    def __init__(self):
        return

    def sendpickle(self, pickled_event):
        return


class UIClientStub():
    """ Class used as specification for UI event handler stub objects """
    def __init__(self):
        self.event = None


class EventHandlingTest(unittest.TestCase):
    """ Event handling test class """
    _threadlock_test_calls = []

    def setUp(self):
        self._test_process = Mock()
        ui_client1 = UIClientStub()
        ui_client2 = UIClientStub()
        self._test_ui1 = Mock(wraps=ui_client1)
        self._test_ui2 = Mock(wraps=ui_client2)
        importlib.reload(bb.event)

    def _create_test_handlers(self):
        """ Method used to create a test handler ordered dictionary """
        test_handlers = bb.compat.OrderedDict()
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
        cleanDict = bb.compat.OrderedDict()
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
        self._test_process.event_handler.assert_called_once_with(event)

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
        expected_event_handler1 = [call(event1)]
        expected_event_handler2 = [call(event1),
                                   call(event2),
                                   call(event2)]
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
        expected = [call(event1), call(event2)]
        self.assertEqual(self._test_process.event_handler1.call_args_list,
                         expected)

        # unregister handler and register it only for OperationStarted
        result = bb.event.remove("event_handler1",
                                 self._test_process.event_handler1)
        mask = ["bb.event.OperationStarted"]
        result = bb.event.register("event_handler1",
                                   self._test_process.event_handler1,
                                   mask)
        self.assertEqual(result, bb.event.Registered)
        bb.event.fire_class_handlers(event1, None)
        bb.event.fire_class_handlers(event2, None)
        expected = [call(event1), call(event2), call(event1)]
        self.assertEqual(self._test_process.event_handler1.call_args_list,
                         expected)

        # unregister handler and register it only for OperationCompleted
        result = bb.event.remove("event_handler1",
                                 self._test_process.event_handler1)
        mask = ["bb.event.OperationCompleted"]
        result = bb.event.register("event_handler1",
                                   self._test_process.event_handler1,
                                   mask)
        self.assertEqual(result, bb.event.Registered)
        bb.event.fire_class_handlers(event1, None)
        bb.event.fire_class_handlers(event2, None)
        expected = [call(event1), call(event2), call(event1), call(event2)]
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
        expected = [call(event1)]
        self.assertEqual(self._test_process.event_handler1.call_args_list,
                         expected)
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

    def test_print_ui_queue(self):
        """ Test print_ui_queue method """
        event1 = bb.event.OperationStarted()
        event2 = bb.event.OperationCompleted(total=123)
        bb.event.fire(event1, None)
        bb.event.fire(event2, None)
        logger = logging.getLogger("BitBake")
        logger.addHandler(bb.event.LogHandler())
        logger.info("Test info LogRecord")
        logger.warning("Test warning LogRecord")
        with self.assertLogs("BitBake", level="INFO") as cm:
            bb.event.print_ui_queue()
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

    def test_enable_threadlock(self):
        """ Test enable_threadlock method """
        self._set_threadlock_test_mockups()
        bb.event.enable_threadlock()
        self._set_and_run_threadlock_test_workers()
        # Calls to UI handlers should be in order as all the registered
        # handlers for the event coming from the first worker should be
        # called before processing the event from the second worker.
        self.assertEqual(self._threadlock_test_calls,
                         ["w1_ui1", "w1_ui2", "w2_ui1", "w2_ui2"])

    def test_disable_threadlock(self):
        """ Test disable_threadlock method """
        self._set_threadlock_test_mockups()
        bb.event.disable_threadlock()
        self._set_and_run_threadlock_test_workers()
        # Calls to UI handlers should be intertwined together. Thanks to the
        # delay in the registered handlers for the event coming from the first
        # worker, the event coming from the second worker starts being
        # processed before finishing handling the first worker event.
        self.assertEqual(self._threadlock_test_calls,
                         ["w1_ui1", "w2_ui1", "w1_ui2", "w2_ui2"])
