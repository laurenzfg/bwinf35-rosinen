#include <iostream>
#include <random>

using namespace std;

int main()
{
    int n;
    cin >> n;

	std::default_random_engine re;
	uniform_int_distribution<int> node(0,n-1);
	uniform_real_distribution<double> val(-1000.0,+1000.0);

    cout << n << "\n";
    for (int i = 0; i < n; i++) {
    	cout << i << " " << val(re) << "\n";
    }
    for (int i = 0; i < n*10; i++) {
    	cout << node(re) << " " << node(re) << "\n";
    }
    return 0;
}
