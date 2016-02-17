#include "rxcpp/rx.hpp"

#include <cctype>
#include <clocale>

namespace rx=rxcpp;
namespace rxsub=rxcpp::subjects;
namespace rxu=rxcpp::util;


int main()
{
	auto keys = rx::observable<>::create<int>
		([](rx::subscriber<int> dest) {
			for (;;)
			{
				int key = std::cin.get();
				dest.on_next(key);
			}
		})
		.publish();

	auto a = keys
						.filter([](int key){ return std::tolower(key) == 'a'; });
	auto g = keys
						.filter([](int key){ return std::tolower(key) == 'g'; });

	a.merge(g)
		.subscribe([](int key)
		{
			std::cout << key << std::endl;
		});

	keys.connect();

	return 0;
}
