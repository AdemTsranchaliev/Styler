package softuniBlog.storage;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.util.stream.Stream;

public interface StorageService {

    void init();

    void store(MultipartFile file,int id,String extention);

    void storeProfilePicture(MultipartFile file,int id,String extention);

    Stream<Path> loadAll();

    Path load(String filename);

    Resource loadAsResource(String filename);


}
