package com.yunkairichu.cike.main;

/**
 * Created by vida2009 on 2015/5/27.
 */
//public class ActivityTakePhoto extends Activity implements SurfaceHolder.Callback{
//    private ImageView back, position;//���غ��л�ǰ��������ͷ
//    private SurfaceView surface;
//    private ImageButton shutter;//����
//    private SurfaceHolder holder;
//    private Camera camera;//�������
//    private String filepath = "";//��Ƭ����·��
//    private int cameraPosition = 1;//0����ǰ������ͷ��1�����������ͷ
//
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//        requestWindowFeature(Window.FEATURE_NO_TITLE);//û�б���
//        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//����ȫ��
//        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);//���չ�����Ļһֱ���ڸ���
//        //�����ֻ���Ļ����һ����7��
//        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
//        //SCREEN_ORIENTATION_BEHIND�� �̳�Activity��ջ�е�ǰActivity������Ǹ�Activity�ķ���
//        //SCREEN_ORIENTATION_LANDSCAPE�� ����(�羰��) ����ʾʱ��ȴ��ڸ߶�
//        //SCREEN_ORIENTATION_PORTRAIT�� ���� (Ф����) �� ��ʾʱ�߶ȴ��ڿ��
//        //SCREEN_ORIENTATION_SENSOR  ��������Ӧ����������Ļ�ĳ���,��ȡ�����û���γ����豸,���豸����תʱ�������֮�ں���������֮��仯
//        //SCREEN_ORIENTATION_NOSENSOR�� ���������Ӧ����������ʾ�����������Ӧ���޹أ������û������ת�豸��ʾ���򶼲������Ÿı�("unspecified"���ó���)
//        //SCREEN_ORIENTATION_UNSPECIFIED�� δָ������ΪĬ��ֵ����Androidϵͳ�Լ�ѡ���ʵ��ķ���ѡ������Ӿ����豸�����������������˲�ͬ���豸���в�ͬ�ķ���ѡ��
//        //SCREEN_ORIENTATION_USER�� �û���ǰ����ѡ����
//
//        setContentView(R.layout.activity_take_photo);
//
//        back = (ImageView) findViewById(R.id.camera_back);
//        position = (ImageView) findViewById(R.id.camera_position);
//        surface = (SurfaceView) findViewById(R.id.camera_surface);
//        shutter = (ImageButton) findViewById(R.id.camera_shutter);
//        holder = surface.getHolder();//��þ��
//        holder.addCallback(this);//��ӻص�
//        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);//surfaceview��ά���Լ��Ļ��������ȴ���Ļ��Ⱦ���潫�������͵��û���ǰ
//
//        //���ü���
//        back.setOnClickListener(listener);
//        position.setOnClickListener(listener);
//        shutter.setOnClickListener(listener);
//    }
//
//    //��Ӧ����¼�
//    View.OnClickListener listener = new View.OnClickListener() {
//        @Override
//        public void onClick(View v) {
//            // TODO Auto-generated method stub
//            switch (v.getId()) {
//                case R.id.camera_back:
//                    //����
//                    MyCameraActivity.this.finish();
//                    break;
//
//                case R.id.camera_position:
//                    //�л�ǰ������ͷ
//                    int cameraCount = 0;
//                    Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
//                    cameraCount = Camera.getNumberOfCameras();//�õ�����ͷ�ĸ���
//
//                    for(int i = 0; i < cameraCount; i   ) {
//                        Camera.getCameraInfo(i, cameraInfo);//�õ�ÿһ������ͷ����Ϣ
//                        if(cameraPosition == 1) {
//                            //�����Ǻ��ã����Ϊǰ��
//                            if(cameraInfo.facing  == Camera.CameraInfo.CAMERA_FACING_FRONT) {//��������ͷ�ķ�λ��CAMERA_FACING_FRONTǰ��      CAMERA_FACING_BACK����
//                                camera.stopPreview();//ͣ��ԭ������ͷ��Ԥ��
//                                camera.release();//�ͷ���Դ
//                                camera = null;//ȡ��ԭ������ͷ
//                                camera = Camera.open(i);//�򿪵�ǰѡ�е�����ͷ
//                                try {
//                                    camera.setPreviewDisplay(holder);//ͨ��surfaceview��ʾȡ������
//                                } catch (IOException e) {
//                                    // TODO Auto-generated catch block
//                                    e.printStackTrace();
//                                }
//                                camera.startPreview();//��ʼԤ��
//                                cameraPosition = 0;
//                                break;
//                            }
//                        } else {
//                            //������ǰ�ã� ���Ϊ����
//                            if(cameraInfo.facing  == Camera.CameraInfo.CAMERA_FACING_BACK) {//��������ͷ�ķ�λ��CAMERA_FACING_FRONTǰ��      CAMERA_FACING_BACK����
//                                camera.stopPreview();//ͣ��ԭ������ͷ��Ԥ��
//                                camera.release();//�ͷ���Դ
//                                camera = null;//ȡ��ԭ������ͷ
//                                camera = Camera.open(i);//�򿪵�ǰѡ�е�����ͷ
//                                try {
//                                    camera.setPreviewDisplay(holder);//ͨ��surfaceview��ʾȡ������
//                                } catch (IOException e) {
//                                    // TODO Auto-generated catch block
//                                    e.printStackTrace();
//                                }
//                                camera.startPreview();//��ʼԤ��
//                                cameraPosition = 1;
//                                break;
//                            }
//                        }
//
//                    }
//                    break;
//
//                case R.id.camera_shutter:
//                    //����
//                    camera.autoFocus(new Camera.AutoFocusCallback() {//�Զ��Խ�
//                        @Override
//                        public void onAutoFocus(boolean success, Camera camera) {
//                            // TODO Auto-generated method stub
//                            if(success) {
//                                //���ò�����������
//                                Camera.Parameters params = camera.getParameters();
//                                params.setPictureFormat(PixelFormat.JPEG);//ͼƬ��ʽ
//                                params.setPreviewSize(800, 480);//ͼƬ��С
//                                camera.setParameters(params);//���������õ��ҵ�camera
//                                camera.takePicture(null, null, jpeg);//�����㵽����Ƭ���Զ���Ķ���
//                            }
//                        }
//                    });
//                    break;
//            }
//        }
//    };
//
//    /*surfaceHolder����ϵͳ�ṩ��һ����������surfaceView��һ�����󣬶���ͨ��surfaceView.getHolder()�����������á�
//     Camera�ṩһ��setPreviewDisplay(SurfaceHolder)�ķ���������*/
//
//    //SurfaceHolder.Callback,���Ǹ�holder������ʾsurfaceView ���ݵĽӿ�,������ʵ������3������
//    @Override
//    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
//        // TODO Auto-generated method stub
//
//    }
//
//    @Override
//    public void surfaceCreated(SurfaceHolder holder) {
//        // TODO Auto-generated method stub
//        //��surfaceview����ʱ�������
//        if(camera == null) {
//            camera = Camera.open();
//            try {
//                camera.setPreviewDisplay(holder);//ͨ��surfaceview��ʾȡ������
//                camera.startPreview();//��ʼԤ��
//            } catch (IOException e) {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//            }
//        }
//    }
//
//    @Override
//    public void surfaceDestroyed(SurfaceHolder holder) {
//        // TODO Auto-generated method stub
//        //��surfaceview�ر�ʱ���ر�Ԥ�����ͷ���Դ
//        camera.stopPreview();
//        camera.release();
//        camera = null;
//        holder = null;
//        surface = null;
//    }
//
//    //����jpegͼƬ�ص����ݶ���
//    Camera.PictureCallback jpeg = new Camera.PictureCallback() {
//        @Override
//        public void onPictureTaken(byte[] data, Camera camera) {
//            // TODO Auto-generated method stub
//            try {
//                Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
//                //�Զ����ļ�����·��  ������ʱ����������
//                filepath = "/sdcard/Messages/MyPictures/"   new SimpleDateFormat("yyyyMMddHHmmss").format(new Date())   ".jpg";
//                File file = new File(filepath);
//                BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
//                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);//��ͼƬѹ����������
//                bos.flush();// ˢ�´˻������������
//                bos.close();// �رմ���������ͷ�������йص�����ϵͳ��Դ
//                camera.stopPreview();//�ر�Ԥ�� ��������
//                camera.startPreview();//���ݴ�����������ʼԤ��
//                bitmap.recycle();//����bitmap�ռ�
//            } catch (Exception e) {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//            }
//        }
//    };
//}
