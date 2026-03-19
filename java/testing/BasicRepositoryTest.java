@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@ContextConfiguration(initializers = PostgreSQLContainerInitializer.class)
@ActiveProfiles("test")
public abstract class BaseRepositoryTest {

    @Autowired
    private TestRepository testRepository;

    @AfterEach
    void resetDb() {
        testRepository.resetAllTablesForTesting();
    }

}