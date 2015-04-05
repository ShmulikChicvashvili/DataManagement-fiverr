package test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ FriendshipsDatabaseTest.class,
		PaidActivitiesDatabaseTest.class, UsersDatabaseTest.class })
public class AllTests {

}
