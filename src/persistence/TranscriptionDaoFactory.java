package persistence;

public class TranscriptionDaoFactory {

    private static TranscriptionDaoFactory instance = new TranscriptionDaoFactory();
    private Class<? extends TranscriptionDao> transcriptionDaoImpl = InMemoryTranscriptionDao.class;

    private TranscriptionDaoFactory() {
    }

    public static TranscriptionDaoFactory getInstance() {
        return instance;
    }

    public TranscriptionDao getTranscriptionDao() {
        try {
            return transcriptionDaoImpl.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Failed to create TranscriptionDao instance.", e);
        }
    }

    public void setTranscriptionDaoImpl(Class<? extends TranscriptionDao> transcriptionDaoImpl) {
        this.transcriptionDaoImpl = transcriptionDaoImpl;
    }
}
