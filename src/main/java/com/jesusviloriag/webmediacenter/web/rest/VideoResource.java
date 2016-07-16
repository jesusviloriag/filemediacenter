package com.jesusviloriag.webmediacenter.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.jesusviloriag.webmediacenter.config.JHipsterProperties;
import com.jesusviloriag.webmediacenter.domain.Video;
import com.jesusviloriag.webmediacenter.repository.VideoRepository;
import com.jesusviloriag.webmediacenter.web.rest.util.HeaderUtil;
import com.jesusviloriag.webmediacenter.web.rest.util.PaginationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.servlet.ServletContext;
import javax.validation.Valid;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing Video.
 */
@RestController
@RequestMapping("/api")
public class VideoResource {

    private final Logger log = LoggerFactory.getLogger(VideoResource.class);

    @Inject
    private ServletContext servletContext;

    @Inject
    private VideoRepository videoRepository;

    @Inject
    private JHipsterProperties jHipsterProperties;

    /**
     * POST  /videos : Create a new video.
     *
     * @param video the video to create
     * @return the ResponseEntity with status 201 (Created) and with body the new video, or with status 400 (Bad Request) if the video has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/videos",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Video> createVideo(@Valid @RequestBody Video video) throws URISyntaxException {
        log.debug("REST request to save Video : {}", video);
        if (video.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("video", "idexists", "A new video cannot already have an ID")).body(null);
        }

        String direccion = servletContext.getRealPath(jHipsterProperties.getFile().getUrl()) + "/videos/" + video.getNombreArchivo();
        direccion = direccion.replace("/",File.separator);

        Video result = null;

        try {

            File file = new File(direccion);

            // if file doesnt exists, then create it
            if (!file.exists()) {
                file.createNewFile();
            }

            FileOutputStream fos = new FileOutputStream(file);
            fos.write(video.getArchivo(), 0, video.getArchivo().length);
            fos.flush();
            fos.close();

            video.setArchivo(null);
            video.setDireccionEnServidor("content/videos/" + video.getNombreArchivo());
            if((video.getTitulo() == null)||(video.getTitulo().equals("")))
                video.setTitulo(video.getNombreArchivo());

            if((video.getAno() == null)||(video.getAno() == 0)) {
                java.util.Date date = new java.util.Date(); // your date
                Calendar cal = Calendar.getInstance();
                cal.setTime(date);
                Integer year = cal.get(Calendar.YEAR); //Your year, kind sir

                video.setAno(year);
            }

            result = videoRepository.save(video);

            System.out.println("Done");

        } catch (IOException e) {
            e.printStackTrace();
        }

        return ResponseEntity.created(new URI("/api/videos/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("video", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /videos : Updates an existing video.
     *
     * @param video the video to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated video,
     * or with status 400 (Bad Request) if the video is not valid,
     * or with status 500 (Internal Server Error) if the video couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/videos",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Video> updateVideo(@Valid @RequestBody Video video) throws URISyntaxException {
        log.debug("REST request to update Video : {}", video);
        if (video.getId() == null) {
            return createVideo(video);
        }

        String direccion = servletContext.getRealPath(jHipsterProperties.getFile().getUrl()) + "/videos/" + video.getNombreArchivo();
        direccion = direccion.replace("/",File.separator);

        Video result = null;

        try {

            File file = new File(direccion);

            // if file doesnt exists, then create it
            if (!file.exists()) {
                file.createNewFile();
            }

            FileOutputStream fos = new FileOutputStream(file);
            fos.write(video.getArchivo(), 0, video.getArchivo().length);
            fos.flush();
            fos.close();

            video.setArchivo(null);
            video.setDireccionEnServidor("content/videos/" + video.getNombreArchivo());
            if((video.getTitulo() == null)||(video.getTitulo().equals("")))
                video.setTitulo(video.getNombreArchivo());

            if((video.getAno() == null)||(video.getAno() == 0)) {
                java.util.Date date = new java.util.Date(); // your date
                Calendar cal = Calendar.getInstance();
                cal.setTime(date);
                Integer year = cal.get(Calendar.YEAR); //Your year, kind sir

                video.setAno(year);
            }

            result = videoRepository.save(video);

            System.out.println("Done");

        } catch (IOException e) {
            e.printStackTrace();
        }

        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("video", video.getId().toString()))
            .body(result);
    }

    /**
     * GET  /videos : get all the videos.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of videos in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @RequestMapping(value = "/videos",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Video>> getAllVideos(Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a page of Videos");
        Page<Video> page = videoRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/videos");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /videos/:id : get the "id" video.
     *
     * @param id the id of the video to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the video, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/videos/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Video> getVideo(@PathVariable Long id) {
        log.debug("REST request to get Video : {}", id);
        Video video = videoRepository.findOne(id);

       /* RandomAccessFile f = null;
        try {
            f = new RandomAccessFile(video.getDireccionEnServidor(), "r");
            byte[] b = new byte[(int)f.length()];
            video.setArchivo(b);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }*/

        return Optional.ofNullable(video)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /videos/:id : delete the "id" video.
     *
     * @param id the id of the video to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @RequestMapping(value = "/videos/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @Transactional
    public ResponseEntity<Void> deleteVideo(@PathVariable Long id) {
        log.debug("REST request to delete Video : {}", id);

        Video video = videoRepository.getOne(id);

        String direccion = servletContext.getRealPath(jHipsterProperties.getFile().getUrl()) + "/videos/" + video.getNombreArchivo();
        direccion = direccion.replace("/",File.separator);

        try{

            File file = new File(direccion);

            if(file.delete()){
                System.out.println(file.getName() + " is deleted!");
            }else{
                System.out.println("Delete operation is failed.");
            }

        }catch(Exception e){

            e.printStackTrace();

        }

        videoRepository.delete(id);

        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("video", id.toString())).build();
    }

}
