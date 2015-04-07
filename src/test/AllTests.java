package test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ UsersDatabaseTest.class, FriendshipsDatabaseTest.class,
		PaidActivitiesDatabaseTest.class, MultiTableTDatabaseTest.class })
public class AllTests {

}
