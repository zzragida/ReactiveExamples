#include "rxcpp/rx.hpp"

namespace rx=rxcpp;
namespace rxu=rxcpp::util;

#include <string>

int main()
{
	auto get_names = [](){ return rx::observable<>::from<std::string>(
			"Matthew",
			"Aaron"
	);};

	std::cout << "==== println stream of std::string ====" << std::endl;
	auto hello_str = [&](){ return get_names().map([](std::string n){
			return "Hello, " + n + "!";
	}).as_dynamic();};

	hello_str().subscribe(rxu::println(std::cout));

	std::cout << "==== println stream of std::tuple ====" << std::endl;
	auto hello_tpl = [&](){ return get_names().map([](std::string n){
			return std::make_tuple("Hello, ", n, "! (", n.size(), ")");
	}).as_dynamic();};

	hello_tpl().subscribe(rxu::println(std::cout));
	hello_tpl().subscribe(rxu::print_followed_by(std::cout, " and "), rxu::endline(std::cout));

	return 0;
}
