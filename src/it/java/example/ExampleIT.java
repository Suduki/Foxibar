package example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import org.junit.Assert;
import org.junit.Test;

public class ExampleIT {

	@Test
	public void testingLongRunning() throws InterruptedException {
		System.out.println("Simulating long work..., this is not appropriate for a unit test");
		long before = System.currentTimeMillis();
		Thread.sleep(TimeUnit.SECONDS.toMillis(15));
		Assert.assertTrue("No time has passed", System.currentTimeMillis() > before);
		System.out.println("Long work passed.");
	}

	@Test
	public void useResource() throws IOException {
		System.out.println(
				"Reading external file, might be appropriate for a unit test, but is for sure possible in an intergation-test");
		try (BufferedReader in = new BufferedReader(
				new InputStreamReader(getClass().getResourceAsStream("/magicnumber.txt")))) {
			String magicNumber = in.readLine();
			Assert.assertEquals("The magic number is wrong", "42", magicNumber);
			System.out.println("Pew magicnumber is still: " + magicNumber);
		}
	}

	@Test
	public void getNumberFromInternet() throws IOException {
		System.out.println(
				"Getting a number from random.org, this is not appropriate for a unit test since it may take long time.");
		System.out.println("It also rely on unreliable IO as in internet, that you have no control over.");
		System.out.println("If you are behind a proxy this test will fail if you don't provide the proxy params:");
		System.out.println("-Dhttps.proxyHost=*server* -Dhttps.proxyPort=*port*");

		URL url = new URL("https://www.random.org/integers/?num=1&min=1&max=100&col=5&base=10&format=plain&rnd=new");

		try (BufferedReader in = new BufferedReader(new InputStreamReader(url.openConnection().getInputStream()))) {
			String line = in.readLine();

			Assert.assertTrue(!line.isEmpty());
			System.out.println("Got number from random.org: " + line);
		}
	}
}
