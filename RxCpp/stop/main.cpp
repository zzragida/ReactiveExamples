#include "rxcpp/rx.hpp"

namespace rx=rxcpp;
namespace rxsub=rxcpp::subjects;
namespace rxu=rxcpp::util;

int main()
{
	{
		auto published_observable = 
				rx::observable<>::range(1)
					.filter([](int i)
					{
						std::cout << i << std::endl;
						std::this_thread::sleep_for(std::chrono::milliseconds(300));
						return true;
					})
					.subscribe_on(rx::observe_on_new_thread())
					.publish();

		auto subscription = published_observable.connect();
		std::this_thread::sleep_for(std::chrono::seconds(1));
		subscription.unsubscribe();
		std::cout << "unsubscribed" << std::endl << std::endl;
	}

	{
		auto published_observable =
				rx::observable<>::interval(std::chrono::milliseconds(300))
					.subscribe_on(rx::observe_on_new_thread())
					.publish();

		published_observable
				.ref_count()
				.take_until(rx::observable<>::timer(std::chrono::seconds(1)))
				.finally([]()
				{
					std::cout << "unsubscribed" << std::endl << std::endl;
				})
				.subscribe([](int i)
				{
					std::cout << i << std::endl;
				});
	}
	
	return 0;
}
