const DatasourceRadio = () => {
  return (
    <>
      <form
        id="source-form"
        class="flex justify-center space-x-4 mt-6 text-xl gap-4 mb-4"
      >
        <input
          class="btn w-32 h-16"
          type="radio"
          name="data-source"
          aria-label="Livestream"
        />
        <input
          class="btn w-32 h-16"
          type="radio"
          name="data-source"
          aria-label="Query"
        />
      </form>
    </>
  );
};

export default DatasourceRadio;
