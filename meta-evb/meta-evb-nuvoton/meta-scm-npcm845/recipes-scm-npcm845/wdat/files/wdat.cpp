#include <sys/types.h>
#include <sys/stat.h>
#include <sys/time.h>
#include <unistd.h>
#include <poll.h>
#include <fcntl.h>
#include <stdio.h>
#include <signal.h>
#include <time.h>
#include <nlohmann/json.hpp>

#include <filesystem>
#include <fstream>
#include <sdbusplus/message.hpp>
#include <sdbusplus/bus.hpp>
#include <phosphor-logging/lg2.hpp>

static constexpr char const* ipmiSELObject = "xyz.openbmc_project.Logging.IPMI";
static constexpr char const* ipmiSELPath = "/xyz/openbmc_project/Logging/IPMI";
static constexpr char const* ipmiSELAddInterface =
	"xyz.openbmc_project.Logging.IPMI";
static constexpr char const* watchdogPath =
	"/xyz/openbmc_project/watchdog/host0";
static const std::string ipmiSELAddMessage = "WDAT SEL";

static int kcs_fd;
static timer_t wdat_timer;
static char timeout = 30;
static char wdat_state = 0;
static int refresh_count = 0;

enum wdat_wdat_state {
	STATE_STOP = 0,
	STATE_RUN = 1
};
enum wdat_actions {
	WDAT_START,
	WDAT_STOP,
        WDAT_RESET,
	WDAT_QUERY,
	WDAT_GET_TIMEOUT,
	WDAT_SET_TIMEOUT,
	WDAT_GET_RUN_STATE,
	WDAT_GET_STOP_STATE,
};
std::map<int, int> action_map;

static int loadActionValues()
{
	const std::string configFilePath =
		"/usr/share/wdat/action.json";
	std::ifstream configFile(configFilePath.c_str());
	if (!configFile.is_open())
	{
		lg2::error("loadActionValues: Cannot open config path \'{PATH}\'",
				   "PATH", configFilePath);
		return -1;
	}
	auto jsonData = nlohmann::json::parse(configFile, nullptr, true, true);

	if (jsonData.is_discarded())
	{
		lg2::error("Power config readings JSON parser failure");
		return -1;
	}
	action_map[WDAT_START] = jsonData.value("start", 0);
	action_map[WDAT_STOP] = jsonData.value("stop", 0);
	action_map[WDAT_RESET] = jsonData.value("reset", 0);
	action_map[WDAT_QUERY] = jsonData.value("query", 0);
	action_map[WDAT_GET_TIMEOUT] = jsonData.value("get_timeout", 0);
	action_map[WDAT_SET_TIMEOUT] = jsonData.value("set_timeout", 0);
	action_map[WDAT_GET_RUN_STATE] = jsonData.value("get_run", 0);
	action_map[WDAT_GET_STOP_STATE] = jsonData.value("get_stop", 0);

	return 0;
}

void addSEL(char eventdata1, char eventdata2, char eventdata3)
{
	using namespace sdbusplus;
	const std::vector<uint8_t> eventData = {eventdata1, eventdata2, eventdata3};
	uint16_t genId = 0x2000; // byte 1 0x20 BMC
	auto bus = bus::new_default_system();
	auto m = bus.new_method_call(ipmiSELObject, ipmiSELPath, ipmiSELAddInterface, "IpmiSelAdd");

	m.append(ipmiSELAddMessage, watchdogPath, eventData, true, genId);
	try
	{	bus.call(m);
	}
	catch (sdbusplus::exception_t& e)
	{
		lg2::error(e.what());
	}
}

int kcs_readbyte(int fd, char *byte)
{
	struct pollfd pfd;
	int r;

	pfd.fd = kcs_fd;
	pfd.events = POLLIN;
	while (1) {
		r = poll(&pfd, 1, 5000);

		if (r < 0) {
			break;
		}

		if (r == 0 || !(pfd.revents & POLLIN)) {
			continue;
		}

		r = read(pfd.fd, byte, 1);
		if (r <= 0) {
			continue;
		}
		break;
	}

	return r;
}

void expired(union sigval timer_data){
	//printf("wdat expired\n");
	addSEL(0, 0x0F, 0);
	lg2::info("wdat expired");
	timer_delete(wdat_timer);
	wdat_state = STATE_STOP;
}

int wdat_action_handler(char action)
{
	struct itimerspec current;
	struct sigevent sev = { 0 };
	struct itimerspec its;
	char value;
	int n;

	its.it_value.tv_sec  = timeout,
	its.it_value.tv_nsec = 0,
	its.it_interval.tv_sec  = 1,
	its.it_interval.tv_nsec = 0;

	if (action == action_map[WDAT_START]) {
		sev.sigev_notify = SIGEV_THREAD;
		sev.sigev_notify_function = &expired;
		sev.sigev_value.sival_ptr = NULL;
		timer_create(CLOCK_REALTIME, &sev, &wdat_timer);
		timer_settime(wdat_timer, 0, &its, NULL);
		wdat_state = STATE_RUN;
		addSEL(0x0B, 0x0F, 0);
		lg2::info("wdat timer started");
	}
	else if (action == action_map[WDAT_RESET]) {
		its.it_value.tv_sec  = timeout,
		timer_settime(wdat_timer, 0, &its, NULL);
		if (++refresh_count == 5000) {
			addSEL(0x1D, 0x0F, 0);
			refresh_count = 0;
		}
	}
	else if (action == action_map[WDAT_STOP]) {
		timer_delete(wdat_timer);
		wdat_state = STATE_STOP;
		addSEL(0x0D, 0x0F, 0);
		lg2::info("wdat timer stopped");
	}
	else if (action == action_map[WDAT_QUERY]) {
		if (timer_gettime(wdat_timer, &current) == 0)
			value = current.it_value.tv_sec;
		else
			value = 0;
		n = write(kcs_fd, &value, 1);
	}
	else if (action == action_map[WDAT_GET_TIMEOUT]) {
		n = write(kcs_fd, &timeout, 1);
	}
	else if (action == action_map[WDAT_SET_TIMEOUT]) {
		n = kcs_readbyte(kcs_fd, &value);
		if (n > 0)
			timeout = value;
	}
	else if (action == action_map[WDAT_GET_RUN_STATE]) {
		n = write(kcs_fd, &wdat_state, 1);
	}
	else if (action == action_map[WDAT_GET_STOP_STATE]) {
		value = !wdat_state;
		n = write(kcs_fd, &value, 1);
	}
	else {
		lg2::info("wdat: invalid instruction");
		addSEL(0x1A, 0x0F, action);
	}
	return 0;
}

int main(int argc, char *argv[])
{
	struct pollfd pfd;
	char action;
	int r;

	if (loadActionValues())
		return -1;

	kcs_fd = open(argv[1], O_RDWR | O_NONBLOCK);
	if (kcs_fd < 0) {
		return -1;
	}
	pfd.fd = kcs_fd;
	pfd.events = POLLIN;

	/* Timer created */
	addSEL(0x09, 0x0F, 0);

	while (1) {
		r = kcs_readbyte(kcs_fd, &action);
		if (r < 0) {
			lg2::error("fail to read byte from KCS");
			addSEL(0x1C, 0x0F, 0);
			break;
		}

		wdat_action_handler(action);
	}

	close(pfd.fd);
	addSEL(0x1E, 0x0F, 0);
	return 0;
}
