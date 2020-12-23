/*
 * Copyright (c) 2020 Ampere Computing LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

#include <phosphor-logging/elog-errors.hpp>
#include <phosphor-logging/elog.hpp>
#include <phosphor-logging/log.hpp>
#include <sdbusplus/bus.hpp>
#include <sdbusplus/message.hpp>

#include <boost/algorithm/string/predicate.hpp>
#include <boost/algorithm/string/replace.hpp>
#include <boost/asio/posix/stream_descriptor.hpp>
#include <boost/container/flat_map.hpp>
#include <boost/container/flat_set.hpp>
#include <gpiod.hpp>
#include <sdbusplus/asio/object_server.hpp>

#include "utils.hpp"

namespace tempevent_log {

/*----------------------------------------------------------------------------
 * @fn help
 *
 * @brief Display help for this application
 * @params  name [IN] - Application name
 *--------------------------------------------------------------------------*/
inline static void help(char *name)
{
  printf("%s is app to save Log for Hightemp/Overtemp event from SCP \n"
        "Example : \n"
        "%s Hightemp start : to save log for hightemp event starting"
        "%s Hightemp stop : to save log for hightemp event stoping"
        "%s Overtemp : to save log for overtemp event"
        , name,name,name,name);
}

inline static void sendLogtoRedfish(std::string msgId) {
  // for IPMI OEM message
  std::vector<uint8_t> eventData(selOemDataMaxSize, 0xFF);

  // 3 bytes for IANA
  eventData[0] = 0x3A;
  eventData[1] = 0xCD;
  eventData[2] = 0x00;

  std::string msg = "Hightemp/Overtemp Event";
  std::string severity = "Critical";
  std::string redMsgID = "OpenBMC.0.1.AmpereCritical.Critical";

  selAddOemRecord(msg, LOG_ALERT, eventData, IPMI_SEL_OEM_RECORD_TYPE,
                "REDFISH_MESSAGE_ID=%s",redMsgID.c_str(),
                "REDFISH_MESSAGE_ARGS=%s Event,%s", msgId.c_str(),severity.c_str());
}

}  // namespace tempevent_log

int main(int argc, char *argv[]) {

  std::string message;

  // Parse argurment
  if ((argc <= 1) || (argc >3)) {
    tempevent_log::help(argv[0]);
    return -1;
  } else {
    message = argv[1];
    if(argv[2]!=NULL){
      message = message + " " + argv[2];
    }
  }

  tempevent_log::sendLogtoRedfish(message.c_str());

  return 0;
}
