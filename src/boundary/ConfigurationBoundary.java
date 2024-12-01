package boundary;

import persistence.TranscriptionDaoFactory;
import persistence.InMemoryTranscriptionDao;
import persistence.PersistenceProvider;

public class ConfigurationBoundary {

    TranscriptionDaoFactory transcriptionDaoFactory = TranscriptionDaoFactory.getInstance();
    
    public void setPersistenceProvider(String provider) {
        if (provider.equals(PersistenceProvider.IN_MEMORY.toString())) {
            transcriptionDaoFactory.setTranscriptionDaoImpl(InMemoryTranscriptionDao.class);
        }
    }
}
