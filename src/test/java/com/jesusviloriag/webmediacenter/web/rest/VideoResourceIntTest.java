package com.jesusviloriag.webmediacenter.web.rest;

import com.jesusviloriag.webmediacenter.MediaCenterApp;
import com.jesusviloriag.webmediacenter.domain.Video;
import com.jesusviloriag.webmediacenter.repository.VideoRepository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.hamcrest.Matchers.hasItem;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Base64Utils;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


/**
 * Test class for the VideoResource REST controller.
 *
 * @see VideoResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = MediaCenterApp.class)
@WebAppConfiguration
@IntegrationTest
public class VideoResourceIntTest {

    private static final String DEFAULT_TITULO = "AAAAA";
    private static final String UPDATED_TITULO = "BBBBB";

    private static final Integer DEFAULT_ANO = 1;
    private static final Integer UPDATED_ANO = 2;

    private static final byte[] DEFAULT_ARCHIVO = TestUtil.createByteArray(1, "0");
    private static final byte[] UPDATED_ARCHIVO = TestUtil.createByteArray(2, "1");
    private static final String DEFAULT_ARCHIVO_CONTENT_TYPE = "image/jpg";
    private static final String UPDATED_ARCHIVO_CONTENT_TYPE = "image/png";
    private static final String DEFAULT_DIRECCION_EN_SERVIDOR = "AAAAA";
    private static final String UPDATED_DIRECCION_EN_SERVIDOR = "BBBBB";

    @Inject
    private VideoRepository videoRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restVideoMockMvc;

    private Video video;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        VideoResource videoResource = new VideoResource();
        ReflectionTestUtils.setField(videoResource, "videoRepository", videoRepository);
        this.restVideoMockMvc = MockMvcBuilders.standaloneSetup(videoResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        video = new Video();
        video.setTitulo(DEFAULT_TITULO);
        video.setAno(DEFAULT_ANO);
        video.setArchivo(DEFAULT_ARCHIVO);
        video.setArchivoContentType(DEFAULT_ARCHIVO_CONTENT_TYPE);
        video.setDireccionEnServidor(DEFAULT_DIRECCION_EN_SERVIDOR);
    }

    @Test
    @Transactional
    public void createVideo() throws Exception {
        int databaseSizeBeforeCreate = videoRepository.findAll().size();

        // Create the Video

        restVideoMockMvc.perform(post("/api/videos")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(video)))
                .andExpect(status().isCreated());

        // Validate the Video in the database
        List<Video> videos = videoRepository.findAll();
        assertThat(videos).hasSize(databaseSizeBeforeCreate + 1);
        Video testVideo = videos.get(videos.size() - 1);
        assertThat(testVideo.getTitulo()).isEqualTo(DEFAULT_TITULO);
        assertThat(testVideo.getAno()).isEqualTo(DEFAULT_ANO);
        assertThat(testVideo.getArchivo()).isEqualTo(DEFAULT_ARCHIVO);
        assertThat(testVideo.getArchivoContentType()).isEqualTo(DEFAULT_ARCHIVO_CONTENT_TYPE);
        assertThat(testVideo.getDireccionEnServidor()).isEqualTo(DEFAULT_DIRECCION_EN_SERVIDOR);
    }

    @Test
    @Transactional
    public void checkArchivoIsRequired() throws Exception {
        int databaseSizeBeforeTest = videoRepository.findAll().size();
        // set the field null
        video.setArchivo(null);

        // Create the Video, which fails.

        restVideoMockMvc.perform(post("/api/videos")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(video)))
                .andExpect(status().isBadRequest());

        List<Video> videos = videoRepository.findAll();
        assertThat(videos).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkDireccionEnServidorIsRequired() throws Exception {
        int databaseSizeBeforeTest = videoRepository.findAll().size();
        // set the field null
        video.setDireccionEnServidor(null);

        // Create the Video, which fails.

        restVideoMockMvc.perform(post("/api/videos")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(video)))
                .andExpect(status().isBadRequest());

        List<Video> videos = videoRepository.findAll();
        assertThat(videos).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllVideos() throws Exception {
        // Initialize the database
        videoRepository.saveAndFlush(video);

        // Get all the videos
        restVideoMockMvc.perform(get("/api/videos?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(video.getId().intValue())))
                .andExpect(jsonPath("$.[*].titulo").value(hasItem(DEFAULT_TITULO.toString())))
                .andExpect(jsonPath("$.[*].ano").value(hasItem(DEFAULT_ANO)))
                .andExpect(jsonPath("$.[*].archivoContentType").value(hasItem(DEFAULT_ARCHIVO_CONTENT_TYPE)))
                .andExpect(jsonPath("$.[*].archivo").value(hasItem(Base64Utils.encodeToString(DEFAULT_ARCHIVO))))
                .andExpect(jsonPath("$.[*].direccionEnServidor").value(hasItem(DEFAULT_DIRECCION_EN_SERVIDOR.toString())));
    }

    @Test
    @Transactional
    public void getVideo() throws Exception {
        // Initialize the database
        videoRepository.saveAndFlush(video);

        // Get the video
        restVideoMockMvc.perform(get("/api/videos/{id}", video.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(video.getId().intValue()))
            .andExpect(jsonPath("$.titulo").value(DEFAULT_TITULO.toString()))
            .andExpect(jsonPath("$.ano").value(DEFAULT_ANO))
            .andExpect(jsonPath("$.archivoContentType").value(DEFAULT_ARCHIVO_CONTENT_TYPE))
            .andExpect(jsonPath("$.archivo").value(Base64Utils.encodeToString(DEFAULT_ARCHIVO)))
            .andExpect(jsonPath("$.direccionEnServidor").value(DEFAULT_DIRECCION_EN_SERVIDOR.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingVideo() throws Exception {
        // Get the video
        restVideoMockMvc.perform(get("/api/videos/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateVideo() throws Exception {
        // Initialize the database
        videoRepository.saveAndFlush(video);
        int databaseSizeBeforeUpdate = videoRepository.findAll().size();

        // Update the video
        Video updatedVideo = new Video();
        updatedVideo.setId(video.getId());
        updatedVideo.setTitulo(UPDATED_TITULO);
        updatedVideo.setAno(UPDATED_ANO);
        updatedVideo.setArchivo(UPDATED_ARCHIVO);
        updatedVideo.setArchivoContentType(UPDATED_ARCHIVO_CONTENT_TYPE);
        updatedVideo.setDireccionEnServidor(UPDATED_DIRECCION_EN_SERVIDOR);

        restVideoMockMvc.perform(put("/api/videos")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedVideo)))
                .andExpect(status().isOk());

        // Validate the Video in the database
        List<Video> videos = videoRepository.findAll();
        assertThat(videos).hasSize(databaseSizeBeforeUpdate);
        Video testVideo = videos.get(videos.size() - 1);
        assertThat(testVideo.getTitulo()).isEqualTo(UPDATED_TITULO);
        assertThat(testVideo.getAno()).isEqualTo(UPDATED_ANO);
        assertThat(testVideo.getArchivo()).isEqualTo(UPDATED_ARCHIVO);
        assertThat(testVideo.getArchivoContentType()).isEqualTo(UPDATED_ARCHIVO_CONTENT_TYPE);
        assertThat(testVideo.getDireccionEnServidor()).isEqualTo(UPDATED_DIRECCION_EN_SERVIDOR);
    }

    @Test
    @Transactional
    public void deleteVideo() throws Exception {
        // Initialize the database
        videoRepository.saveAndFlush(video);
        int databaseSizeBeforeDelete = videoRepository.findAll().size();

        // Get the video
        restVideoMockMvc.perform(delete("/api/videos/{id}", video.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<Video> videos = videoRepository.findAll();
        assertThat(videos).hasSize(databaseSizeBeforeDelete - 1);
    }
}
